package com.medsync.cadastroagendamento.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.notificacao")
public class NotificacaoProperties {
    
    private boolean habilitado = true;
    private int lembreteHorasAntecedencia = 24;
    private int lembreteHorasAntecedencia2 = 2;
    private int lembreteHorasAntecedencia3 = 1;
    private String timezone = "America/Sao_Paulo";
    
    public NotificacaoProperties() {
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }
    
    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    public int getLembreteHorasAntecedencia() {
        return lembreteHorasAntecedencia;
    }
    
    public void setLembreteHorasAntecedencia(int lembreteHorasAntecedencia) {
        this.lembreteHorasAntecedencia = lembreteHorasAntecedencia;
    }
    
    public int getLembreteHorasAntecedencia2() {
        return lembreteHorasAntecedencia2;
    }
    
    public void setLembreteHorasAntecedencia2(int lembreteHorasAntecedencia2) {
        this.lembreteHorasAntecedencia2 = lembreteHorasAntecedencia2;
    }
    
    public int getLembreteHorasAntecedencia3() {
        return lembreteHorasAntecedencia3;
    }
    
    public void setLembreteHorasAntecedencia3(int lembreteHorasAntecedencia3) {
        this.lembreteHorasAntecedencia3 = lembreteHorasAntecedencia3;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
