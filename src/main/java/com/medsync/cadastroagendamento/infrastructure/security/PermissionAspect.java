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
}
