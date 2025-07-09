import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AgendadordeConsulta {
    private final List<Consulta> consultas;
    private final Scanner sc = new Scanner(System.in);
    private final UsuarioPaciente pacienteLogado;
    private final Map<String, List<UsuarioPaciente>> listaEspera = new HashMap<>();
    private String medicoEscolhido, dataCancelar, horarioCancelar;
    private int escolhaData = 0;

    public AgendadordeConsulta(UsuarioPaciente pacienteLogado) {
        this.consultas = new ArrayList<>();
        this.pacienteLogado = pacienteLogado;
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
        System.out.print("\n  Datas disponíveis:  \n");
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

        // Verificar todos os horários ocupados
        Set<LocalTime> horariosOcupados = new HashSet<>();

        List<Agendamento> agendamentosMedico = medico.getAgendamentos();
        for (Agendamento ag : agendamentosMedico) {
            if (ag.getData().equals(dataEscolhida)) {
                horariosOcupados.add(ag.getHora());
            }
        }

        // Buscar arquivos recursivamente para encontrar horários ocupados na data
        List<File> arquivosEncontrados = new ArrayList<>();
        buscarArquivos(new File("dados_agendamentos"), arquivosEncontrados, dataEscolhida);

        for (File arquivo : arquivosEncontrados) {
            String[] partes = arquivo.getName().split("_");
            String horaParte = partes[2].replace(".txt", "").replace("-", ":");
            LocalTime horaOcupada = LocalTime.parse(horaParte);
            horariosOcupados.add(horaOcupada);
        }

        LocalTime[] horariosConsultas = {
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(15, 0)
        };

        List<LocalTime> horariosDisponiveis = new ArrayList<>();
        for (LocalTime horario : horariosConsultas) {
            if (!horariosOcupados.contains(horario)) {
                horariosDisponiveis.add(horario);
            }
        }

        if (horariosDisponiveis.isEmpty()) {
            // Lista de espera - adiciona o paciente para essa data
            String chave = dataEscolhida.toString();
            listaEspera.putIfAbsent(chave, new ArrayList<>());
            listaEspera.get(chave).add(pacienteLogado);
            System.out.println("Todos os horários estão ocupados. Você foi adicionado à lista de espera para " + chave);
            return;
        }

        System.out.println("\nHorários disponíveis:");
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
        EscreverArquivo.escreverAgendamento(medico, pacienteLogado, dataEscolhida, horaEscolhida);
        System.out.println("Consulta marcada com sucesso!");
    }

    public void cancelarConsulta() {
        System.out.print("\n=== Cancelar Consulta ===\n");
        System.out.print("\n --- Consultas Marcadas --- \n");
        exibirConsultas();
        System.out.print("Selecione a data da consulta (dd/MM/yyyy): ");
        dataCancelar = sc.nextLine();
        System.out.print("Selecione o horário da consulta (HH:mm): ");
        horarioCancelar = sc.nextLine();
        System.out.print("Selecione o médico: ");
        String medicoCancelar = sc.nextLine();

        UsuarioMedico medico = LerArquivo.buscarMedicoPorNome(medicoCancelar);

        DateTimeFormatter formatoEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(dataCancelar, formatoEntrada);
        String dataFormatada = data.toString();
        String horaFormatada = horarioCancelar.replace(":", "-");

        File arquivoConsultarDeletar = new File(
                "dados_agendamentos/" + medico.getId() + "/" + pacienteLogado.getId() + "_" + dataFormatada + "_"
                        + horaFormatada + ".txt");
        if (arquivoConsultarDeletar.delete()) {
            System.out.println("Consulta cancelada com sucesso!");
        } else {
            System.out.println("Falha ao cancelar consulta.");
        }

        String chave = dataFormatada;
        if (listaEspera.containsKey(chave) && !listaEspera.get(chave).isEmpty()) {
            UsuarioPaciente proximo = listaEspera.get(chave).remove(0);
            System.out.println("Paciente da lista de espera promovido: " + proximo.getNome());
            // Aqui você poderia automaticamente remarcar a consulta para o paciente
            // promovido
        }
    }

    public void exibirMedicos() {
        LerArquivo.listarArquivos(new File("dados_usuarios/medicos"), "medico", pacienteLogado);
    }

    public void exibirConsultas() {
        LerArquivo.listarArquivos(new File("dados_agendamentos"), "consulta", pacienteLogado);
    }

    private void buscarArquivos(File pasta, List<File> lista, LocalDate dataEscolhida) {
        File[] arquivos = pasta.listFiles();
        if (arquivos != null) {
            for (File f : arquivos) {
                if (f.isDirectory()) {
                    buscarArquivos(f, lista, dataEscolhida); // chamada recursiva para subpastas
                } else if (f.getName().endsWith(".txt") && f.getName().contains(dataEscolhida.toString())) {
                    lista.add(f);
                }
            }
        }
    }
}
