import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RealizarConsulta {
    private UsuarioMedico medico;
    private final Scanner sc = new Scanner(System.in);

    public RealizarConsulta(UsuarioMedico medico) {
        this.medico = medico;
    }

    public void realizarConsulta() {
        System.out.print("\n=== Realizar Consulta ===\n");
        System.out.print("\n --- Consultas Marcadas ---\n");

        String caminho = "dados_agendamentos/" + medico.getId() + "/";
        File pasta = new File(caminho);
        LerArquivo.listarArquivos(pasta, "consulta", medico);

        System.out.print("\nSelecione a data (dd/MM/yyyy): ");
        String dataInput = sc.nextLine();

        System.out.print("Selecione o horário (HH:mm): ");
        String horarioConsulta = sc.nextLine();

        System.out.print("\n --- Preencher relatório ---\n");
        System.out.print("ID do paciente: ");
        String idPaciente = sc.nextLine();

        System.out.print("Sintomas: ");
        String sintomas = sc.nextLine();

        System.out.print("Tratamento sugerido: ");
        String tratamentoSugerido = sc.nextLine();

        System.out.print("Exames: ");
        String exames = sc.nextLine();

        System.out.print("Medicamentos: ");
        String medicamentos = sc.nextLine();

        // Formatar data para nome de arquivo
        DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoArquivo = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate data = LocalDate.parse(dataInput, formatoEntrada);
        String dataConsulta = data.format(formatoArquivo);

        // Converter horário para LocalTime
        java.time.LocalTime hora = java.time.LocalTime.parse(horarioConsulta);

        // Buscar paciente pelo ID (usando LerArquivo)
        int idPacienteInt;
        try {
            idPacienteInt = Integer.parseInt(idPaciente);
        } catch (NumberFormatException e) {
            System.out.println("ID do paciente inválido.");
            return;
        }

        UsuarioPaciente paciente = (UsuarioPaciente) LerArquivo.buscarUsuarioPorId(idPacienteInt, "Paciente");
        if (paciente == null) {
            System.out.println("Paciente não encontrado.");
            return;
        }

        // Criar objeto Consulta
        LocalDateTime dataHoraConsulta = LocalDateTime.of(data, hora);
        Consulta consulta = new Consulta(medico, paciente, dataHoraConsulta, StatusConsulta.AGENDADA);

        // Atualizar status para REALIZADA usando o método da classe Consulta
        consulta.atualizarStatusNoArquivo(StatusConsulta.REALIZADA);

        // Montar conteúdo do relatório
        String conteudo = "Paciente ID: " + idPaciente +
                "\nData: " + dataConsulta +
                "\nHorário: " + horarioConsulta +
                "\nSintomas: " + sintomas +
                "\nTratamento sugerido: " + tratamentoSugerido +
                "\nExames: " + exames +
                "\nMedicamentos: " + medicamentos +
                "\nStatus: REALIZADA";

        // Nome e pasta do arquivo de relatório
        String nomeArquivoRelatorio = idPaciente + "_" + dataConsulta + "_" + horarioConsulta.replace(":", "-")
                + "_relatorio.txt";
        String pastaRelatorio = "dados_consultas/" + medico.getId() + "/";
        new File(pastaRelatorio).mkdirs(); // cria pasta se não existir

        // Salvar arquivo do relatório
        EscreverArquivo.escreverEmArquivo(nomeArquivoRelatorio, pastaRelatorio, conteudo);

        System.out.println("Consulta registrada com sucesso.");
    }

    public void visualizarConsultas() {
        System.out.print("\n=== Visualizar Consultas ===\n");
        System.out.print("\n --- Consultas Marcadas ---\n");
        String caminho = "dados_agendamentos/" + medico.getId() + "/";
        File pasta = new File(caminho);
        LerArquivo.listarArquivos(pasta, "consulta", medico);
    }

}
