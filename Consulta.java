import java.time.LocalDateTime; // biblioteca usada para formatar datas e horários em java
import java.time.format.DateTimeFormatter;

public class Consulta {
    private UsuarioMedico medico;
    private UsuarioPaciente paciente;
    private LocalDateTime dataHoraConsulta;
    private StatusConsulta statusConsulta;
    
    public Consulta(UsuarioMedico medico, UsuarioPaciente paciente, LocalDateTime dataHoraConsulta, StatusConsulta statusConsulta) {
        this.medico = medico;
        this.paciente = paciente;
        this.dataHoraConsulta = dataHoraConsulta;
        this.statusConsulta = statusConsulta;
    }

    public UsuarioPaciente getPaciente() {
        return paciente;
    }

    public void setPaciente(UsuarioPaciente paciente) {
        this.paciente = paciente;
    }

    public UsuarioMedico getMedico() {
        return medico;
    }

    public void setMedico(UsuarioMedico medico) {
        this.medico = medico;
    }

    public LocalDateTime getDataHoraConsulta() {
        return dataHoraConsulta;
    }

    public void setDataHoraConsulta(LocalDateTime dataHoraConsulta) {
        this.dataHoraConsulta = dataHoraConsulta;
    }

    public StatusConsulta getStatusConsulta() {
        return statusConsulta;
    }

    public void setStatusConsulta(StatusConsulta statusConsulta) {
        this.statusConsulta = statusConsulta;
    }

}
