package com.medsync.cadastroagendamento.domain.enums;

public enum StatusConsulta {
    
    AGENDADA("AGENDADA", "Agendada"),
    
    CONFIRMADA("CONFIRMADA", "Confirmada"),
    
    EM_ANDAMENTO("EM_ANDAMENTO", "Em Andamento"),
    
    FINALIZADA("FINALIZADA", "Finalizada"),
    
    CANCELADA("CANCELADA", "Cancelada"),
    
    REAGENDADA("REAGENDADA", "Reagendada"),
    
    NAO_COMPARECEU("NAO_COMPARECEU", "Não Compareceu"),
    
    INATIVA("INATIVA", "Inativa");
    
    private final String codigo;
    private final String descricao;
    
    StatusConsulta(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public static StatusConsulta fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return AGENDADA; // Status padrão
        }
        
        for (StatusConsulta statusConsulta : values()) {
            if (statusConsulta.codigo.equalsIgnoreCase(status.trim())) {
                return statusConsulta;
            }
        }
        
        throw new IllegalArgumentException("Status de consulta inválido: " + status);
    }
    
    public boolean isFinal() {
        return this == FINALIZADA || this == CANCELADA || this == NAO_COMPARECEU || this == INATIVA;
    }
    
    public boolean podeCancelar() {
        return this == AGENDADA || this == CONFIRMADA;
    }
    
    public boolean podeReagendar() {
        return this == AGENDADA || this == CONFIRMADA;
    }
}
