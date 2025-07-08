import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Scanner;

public class AgendadordeConsulta {
    private final List<Consulta> consultas;
    Scanner sc = new Scanner(System.in);
    private String medicoEscolhido;
    private int escolhaData = 0;

    public AgendadordeConsulta() {
        this.consultas = new ArrayList<>();
    }

    public void agendarConsulta() {
        System.out.print("\n=== Agendar Consulta ===\n");
        System.out.print("\n --- Médicos Disponíveis --- \n");
        exibirMedicos();

        System.out.print("\nSelecione um médico: ");
        medicoEscolhido = sc.nextLine();
        UsuarioMedico medico = LerArquivo.buscarMedicoPorNome(medicoEscolhido);
        if (medico == null) {
            System.out.print("Médico não encontrado.");
            return;
        }

        LocalDate[] datasConsulta = { LocalDate.now(), LocalDate.now().plusDays(1), LocalDate.now().plusDays(2) };
        System.out.print("  Datas disponíveis:  ");
        for (int i = 0; i < datasConsulta.length; i++) {
            System.out.println((i + 1) + " - " + datasConsulta[i]);
        }

        System.out.print("Selecione a data: ");
        escolhaData = sc.nextInt();
        sc.nextLine();
        if (escolhaData < 1 || escolhaData > 3) {
            System.out.print("Data inválida");
            return;
        }

        LocalDate dataEscolhida = datasConsulta[escolhaData - 1];
        List<Agendamento> agendamentos = medico.getAgendamentos();
        int contadorData = 0;
        for (int i = 0; i < agendamentos.size(); i++) {
            if (agendamentos.get(i).getData().equals(dataEscolhida)) {
                contadorData++;
            }
        }

        if (contadorData >= 3) {
            System.out.println("Limite de consultas atingido para essa data.");
            return;
        }

        LocalTime[] horariosConsultas = {
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(15, 0)
        };

        List<LocalTime> horariosDisponiveis = new ArrayList<>();
        for (int i = 0; i < horariosConsultas.length; i++) {
            boolean horarioOcupado = false;
            for (int j = 0; j < agendamentos.size(); j++) {
                if (agendamentos.get(j).getData().equals(dataEscolhida)
                        && agendamentos.get(j).getHora().equals(horariosConsultas[i])) {
                    horarioOcupado = true;
                    break;
                }
            }

            if (!horarioOcupado) {
                horariosDisponiveis.add(horariosConsultas[i]);
            }
        }

        System.out.println("Horários disponíveis:");
        for (int i = 0; i < horariosDisponiveis.size(); i++) {
            System.out.println((i + 1) + " - " + horariosDisponiveis.get(i));
        }

        System.out.print("Escolha o horário: ");
        int escolhaHora = sc.nextInt();
        sc.nextLine();
        if (escolhaHora < 1 || escolhaHora > horariosDisponiveis.size()) {
            System.out.println("Horário inválido.");
            return;
        }

        LocalTime horaEscolhida = horariosDisponiveis.get(escolhaHora - 1);

        Agendamento novo = new Agendamento(null, dataEscolhida, horaEscolhida);
        medico.adicionarAgendamento(novo);
        System.out.println("Consulta marcada com sucesso!");

    }

    public void exibirMedicos() {
        File medicosDir = new File("dados_usuarios\\medicos");
        File[] arquivos = medicosDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (arquivos != null) {
            for (File arquivo : arquivos) {
                Map<String, String> dados = LerArquivo.lerCamposDoArquivo(arquivo.getAbsolutePath());
                if (dados != null) {
                    System.out.println("Nome: " + dados.get("Nome"));
                    System.out.println("Especialidade: " + dados.get("Especialidade"));
                    System.out.println("Planos: " + dados.get("Planos_de_Saúde_Atendidos"));
                    System.out.println("----------------------------");
                }
            }
        }
    }
}
