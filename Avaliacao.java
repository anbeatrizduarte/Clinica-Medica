import java.io.File;
import java.util.*;

public class Avaliacao {
    private UsuarioPaciente paciente;
    Scanner sc = new Scanner(System.in);

    public Avaliacao(UsuarioPaciente paciente) {
        this.paciente = paciente;
    }

    public void avaliarConsulta() {
        System.out.print("\n=== Avaliar Consulta ===\n");
        System.out.print("\n --- Consultas Realizadas ---\n");
        visualizarConsultas();
        System.out.print("Selecione a data: ");
        String dataConsulta = sc.nextLine();
        System.out.print("Selecione o horário: ");
        String horarioConsulta = sc.nextLine();
        System.out.print("Selecione o médico: ");
        String medicoConsulta = sc.nextLine();

        UsuarioMedico medico = LerArquivo.buscarMedicoPorNome(medicoConsulta);

        System.out.print("Escreva um texto de avaliação: ");
        String textoAvaliação = sc.nextLine();

        System.out.print("\nQuantas estrelas para o médico (Máx. 5): ");
        String estrelasStr = sc.nextLine().replace(",", ".");
        double estrelasEscolhidas = Double.parseDouble(estrelasStr);

        String conteudo = "Usuário: " + paciente.getNome() +
                "\nTexto: " + textoAvaliação +
                "\nNotas: " + getEstrelasString(estrelasEscolhidas);

        String dataFormatada = dataConsulta.replace("/", "-");
        String horarioFormatado = horarioConsulta.replace(":", "-");
        String nomeArquivoRelatorio = medico.getId() + "_" + dataFormatada + "_" + horarioFormatado + "_avaliacao.txt";

        String pastaRelatorio = "dados_avaliacoes/" + medico.getId() + "/";
        new File(pastaRelatorio).mkdirs();
        EscreverArquivo.escreverEmArquivo(nomeArquivoRelatorio, pastaRelatorio, conteudo);
        System.out.println("Avaliação registrada com sucesso.");

    }

    public void visualizarConsultas() {
        String caminho = "dados_agendamentos/";
        File pasta = new File(caminho);
        LerArquivo.listarArquivos(pasta, "consulta", paciente);
    }

    public static String getEstrelasString(double nota) {
        int estrelaCheia = (int) nota;
        boolean meiaEstrela = (nota - estrelaCheia) >= 0.5;
        StringBuilder estrelas = new StringBuilder();

        for (int i = 0; i < estrelaCheia; i++)
            estrelas.append("⭐");
        if (meiaEstrela)
            estrelas.append("½");

        return estrelas.toString();
    }

}
