package com.medsync.cadastroagendamento.infrastructure.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Aspect para interceptar métodos anotados com @RequirePermission
 * e validar se o usuário autenticado possui as permissões necessárias.
 */
@Aspect
@Component
public class PermissionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    
    @Around("@annotation(com.medsync.cadastroagendamento.infrastructure.security.RequirePermission)")
    public Object validatePermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        
        if (annotation == null) {
            return joinPoint.proceed();
        }
        
        String[] requiredPermissions = annotation.value();
        boolean requireAll = annotation.requireAll();
        
        if (requiredPermissions.length == 0) {
            return joinPoint.proceed();
        }
        
        // Verificar se é uma requisição local (do serviço de notificações)
        if (isLocalRequest()) {
            logger.debug("Permitindo acesso local ao método: {}", method.getName());
            return joinPoint.proceed();
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Tentativa de acesso sem autenticação ao método: {}", method.getName());
            throw new SecurityException("Usuário não autenticado");
        }
        
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> userPermissions = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(authority -> authority.startsWith("PERMISSION_"))
            .map(authority -> authority.substring("PERMISSION_".length()))
            .toList();
        
        boolean hasPermission;
        if (requireAll) {
            // Usuário deve ter todas as permissões
            hasPermission = Arrays.stream(requiredPermissions)
                .allMatch(userPermissions::contains);
        } else {
            // Usuário deve ter pelo menos uma das permissões
            hasPermission = Arrays.stream(requiredPermissions)
                .anyMatch(userPermissions::contains);
        }
        
        if (!hasPermission) {
            logger.warn("Usuário {} não possui permissões necessárias para acessar método: {}. " +
                       "Permissões necessárias: {}, Permissões do usuário: {}", 
                       authentication.getName(), method.getName(), 
                       Arrays.toString(requiredPermissions), userPermissions);
            throw new SecurityException("Usuário não possui permissões necessárias para esta operação");
        }
        
        logger.debug("Usuário {} autorizado para acessar método: {}", 
                    authentication.getName(), method.getName());
        
        return joinPoint.proceed();
    }
    
    /**
     * Verifica se a requisição é do serviço de notificações (porta 8082)
     */
    private boolean isLocalRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return false;
            }
            
            HttpServletRequest request = attributes.getRequest();
            String remoteAddr = request.getRemoteAddr();
            String serviceSource = request.getHeader("X-Service-Source");
            String servicePort = request.getHeader("X-Service-Port");
            
            // Verificar se é localhost
            boolean isLocalhost = "127.0.0.1".equals(remoteAddr) || 
                                 "0:0:0:0:0:0:0:1".equals(remoteAddr) ||
                                 "localhost".equals(remoteAddr);
            
            // Verificar se vem do serviço de notificações específico
            boolean isNotificacaoService = "notificacao-api".equals(serviceSource) && "8082".equals(servicePort);
            
            if (isLocalhost && isNotificacaoService) {
                logger.debug("Requisição do serviço de notificações detectada - RemoteAddr: {}, ServiceSource: {}, ServicePort: {}", 
                           remoteAddr, serviceSource, servicePort);
                return true;
            }
            
            return false;
                   
        } catch (Exception e) {
            logger.debug("Erro ao verificar endereço remoto: {}", e.getMessage());
            return false;
        }
    }
}
