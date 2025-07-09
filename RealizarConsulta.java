import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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

        // Montar conteúdo do relatório
        String conteudo = "Paciente ID: " + idPaciente +
                          "\nData: " + dataConsulta +
                          "\nHorário: " + horarioConsulta +
                          "\nSintomas: " + sintomas +
                          "\nTratamento sugerido: " + tratamentoSugerido +
                          "\nExames: " + exames +
                          "\nMedicamentos: " + medicamentos;

        // Nome do arquivo relatório
        String nomeArquivo = idPaciente + "_consultaRealizada_" + dataConsulta + "_" + horarioConsulta.replace(":", "-") + ".txt";

        // Salvar arquivo do relatório
        EscreverArquivo.escreverEmArquivo(nomeArquivo, "dados_consultas/" + medico.getId() + "/", conteudo);

        // Apagar arquivo de agendamento para liberar o horário
        String nomeArquivoAgendamento = idPaciente + "_" + dataConsulta + "_" + horarioConsulta.replace(":", "-") + ".txt";
        File arquivoAgendamento = new File("dados_agendamentos/" + medico.getId() + "/" + nomeArquivoAgendamento);

        if (arquivoAgendamento.exists() && arquivoAgendamento.delete()) {
            System.out.println("Horário liberado após realização da consulta.");
        } else {
            System.out.println("Atenção: arquivo de agendamento não encontrado ou não pôde ser excluído.");
        }

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
