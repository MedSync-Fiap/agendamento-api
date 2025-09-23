package com.medsync.cadastroagendamento.infrastructure.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para validar permissões em métodos de controllers.
 * Permite especificar quais permissões são necessárias para acessar um endpoint.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    
    /**
     * Lista de permissões necessárias para acessar o endpoint.
     * O usuário deve ter pelo menos uma das permissões especificadas.
     */
    String[] value() default {};
    
    /**
     * Se true, o usuário deve ter todas as permissões especificadas.
     * Se false (padrão), o usuário precisa ter pelo menos uma das permissões.
     */
    boolean requireAll() default false;
}
