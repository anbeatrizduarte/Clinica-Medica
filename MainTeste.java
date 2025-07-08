import java.util.Scanner;

public class MainTeste {
    public static void main (String args[]) {
        int opcao = 0;
        AgendadordeConsulta agendador = new AgendadordeConsulta();

        System.out.print("\n  1. Agendar Consulta\n");
        System.out.print("Opção: ");
        Scanner sc = new Scanner(System.in);
        opcao = sc.nextInt();

        

        if(opcao == 1) {
            agendador.agendarConsulta();
        }
    }
}
