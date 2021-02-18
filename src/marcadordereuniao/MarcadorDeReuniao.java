package marcadordereuniao;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MarcadorDeReuniao {
    public  List<Horario> horarios = new ArrayList<Horario>();
    static int numPart;
    static Scanner sc = new Scanner(System.in);
    static LocalDate dataInicial;
    static LocalDate dataFinal;
    
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        ArrayList<String> listaDeParticipantes = new ArrayList<>();
        dataInicial = null; 
        dataFinal = null;
        boolean FimAntesDoInicio = true;
        while(FimAntesDoInicio == true){
        
            System.out.println("[FORMATO: 'dd/mm/aaaa'] Data Inicial:");
            String DataI = sc.next();
            dataInicial = LocalDate.parse(DataI, formatter);
            
            System.out.println("[FORMATO: 'dd/mm/aaaa'] Data Final:");
            String DataF = sc.next();
            dataFinal = LocalDate.parse(DataF, formatter);
            FimAntesDoInicio = dataInvalida(dataInicial, dataFinal);
            if(FimAntesDoInicio){
                System.out.println("A DATA FINAL é anterior a DATA INICIAL, por favor, reajuste as datas.");
            }
        }
        
        MarcadorDeReuniao marcar = new MarcadorDeReuniao();
        marcar.marcarReuniaoEntre (dataInicial, dataFinal, listaDeParticipantes);     
    }    
        
    public static boolean dataInvalida (LocalDate Inicio, LocalDate Fim){
        return Fim.isBefore(Inicio);  
    } 
    
    public void marcarReuniaoEntre(LocalDate dataInicial, LocalDate dataFinal, List <String> listaDeParticipantes){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        System.out.println("Qual é o número de participantes?");
        numPart = sc.nextInt();
        for (int i=1; i<=numPart; i++) {
            System.out.printf("Participante %d: \n", i);
            String participante = sc.next();
            listaDeParticipantes.add(participante); 
	}
        
        for (String p : listaDeParticipantes) {
            LocalDate dataAtual = dataInicial;
            while(dataAtual.isBefore(dataFinal) || dataAtual.equals(dataFinal)){
                System.out.println(p + ", quantos períodos de horários você tem disponíveis para o  dia "+ dataAtual.format(formatter) + "?");
                int quantosHorarios = sc.nextInt();
                LocalDateTime dataAtt = dataAtual.atStartOfDay();
                LocalDateTime inicio = dataAtt;
                LocalDateTime fim = null;
                for(int i=1; i<=quantosHorarios; i++){
                    System.out.println("No seu " + i + "° horário do dia " + dataAtual.format(formatter) + ":");
                    indicaDisponibilidadeDe (p , inicio, fim);    
                }
                dataAtual = dataAtual.plusDays(1);
            }
        }
        
        mostraSobreposicao();     
    }
    
    public void indicaDisponibilidadeDe(String participante, LocalDateTime inicio, LocalDateTime fim){
        LocalDateTime reserva = inicio;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        String reservaS = reserva.format(formatter);
        
        System.out.println("[FORMATO: 'HH:mm'] Qual o horário INICIAL?");
        String hi = sc.next();
        String horarioInicio = reservaS+ " " +hi;
        LocalDateTime hInicio = LocalDateTime.parse(horarioInicio, formatter2);
        
        System.out.println("[FORMATO: 'HH:mm'] Qual o horário FINAL?");
        String hf = sc.next();
        String horarioFim = reservaS+ " " +hf;
        LocalDateTime hFim = LocalDateTime.parse(horarioFim, formatter2);
        
        Horario hini = new Horario(participante, 'i', hInicio);
        Horario hfi = new Horario(participante, 'f', hFim);      
        
        horarios.add(hini);
        horarios.add(hfi);

    }
    
    public void mostraSobreposicao(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dia = dataInicial;
        String[] r = new String[3];
        System.out.println("Este é o relatório de horários de seus participantes.");

        while(dia.isBefore(dataFinal) || dia.equals(dataFinal)){
            System.out.println(" ---------------------- ");
            System.out.println("|  No dia: " +  dia.format(formatter) + "  |");
            System.out.println(" ---------------------- ");
            relatorioDeHorarios(dia);
            System.out.println("------------------------------------------------");

            dia = dia.plusDays(1);
        }
        
        Collections.sort(horarios);
        int quantosIniciosAchou = 0;
        List<String[]> intervalos = new ArrayList<>();
        
        boolean achei= false;
        for (Horario horario : horarios) {            
            if(horario.situacao ==  'i') quantosIniciosAchou++;
            else quantosIniciosAchou--;
            
            if (achei && horario.situacao == 'f'){
                String min=  (horario.hora.getMinute() < 10) ? "0"+horario.hora.getMinute(): horario.hora.getMinute()+"";
                r[2] = horario.hora.getHour()+":"+min;
                achei = false;
                intervalos.add(r);
                r = new String[3];
            }
            
            if (quantosIniciosAchou == numPart){
                r[0] = horario.hora.getDayOfMonth() + "/" + horario.hora.getMonthValue() + "/"  + horario.hora.getYear();
                String min=  (horario.hora.getMinute() < 10) ? "0"+horario.hora.getMinute(): horario.hora.getMinute()+"";
                r[1] = horario.hora.getHour()+":"+ min;
                achei = true;
            }
                    
        }
        System.out.println(" ");
        System.out.println("-----------------------------------------------------------");

        if(intervalos.isEmpty()){
            System.out.println("Não é possível marcar reunião com todos os participantes entre o dia " + dataInicial.format(formatter) + " e " + dataFinal.format(formatter));
        }
        else{
            System.out.println("O(s) horário(s) em que todos seus participantes estão disponíveis são:");
        }
        for (String[] intervalo : intervalos) {
            //se intervalo estiver vazio, printar bonitinho
            
            System.out.println("");
            System.out.println(" ------------------------------------------------------------- ");
            System.out.println("| Dia: " + intervalo[0]+ " | Horário Início: "+ intervalo[1] + " | Horário Final: "+ intervalo[2] + " |");
            System.out.println(" ------------------------------------------------------------- ");

        }
    }
    
    public void relatorioDeHorarios(LocalDate dia){
        String EmailDoMomento = null;
        for (Horario horario : horarios ){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if(dia.format(formatter).equals(horario.hora.toLocalDate().format(formatter))){
                if(!horario.participante.equals(EmailDoMomento)){
                    EmailDoMomento = horario.participante;
                    System.out.println("E-mail: " + horario.participante);
                    if (horario.situacao == 'i'){
                        System.out.println("Horário Inicial");
                    }
                    else{
                        System.out.println("Horário Final");
                    }
                    String min=  (horario.hora.getMinute() < 10) ? "0"+horario.hora.getMinute(): horario.hora.getMinute()+"";
                    System.out.println("Horário: " + horario.hora.getHour() + ":" + min);
                }
                else{
                    if (horario.situacao == 'i'){
                        System.out.println("Horário Inicial");
                    }
                    else{
                        System.out.println("Horário Final");
                    }
                    String min=  (horario.hora.getMinute() < 10) ? "0"+horario.hora.getMinute(): horario.hora.getMinute()+"";
                    System.out.println("Horário: " + horario.hora.getHour() + ":" + min);
                }   
            }
        }   
    }
}
class Horario implements Comparable<Horario> {
    String participante;
    char situacao;
    LocalDateTime hora;

    public Horario(String p, char s, LocalDateTime h) {
        this.participante = p;
        this.situacao = s;
        this.hora = h;
    }

    
    @Override
    public int compareTo(Horario h) {
        if (this.hora.isBefore(h.hora)){
            return -1;
        }
        else if (this.hora.isAfter(h.hora)){
            return 1;
        }
        
        else if (this.hora.isEqual(h.hora) ){
            if (this.situacao  == 'i'&& h.situacao == 'f') {
                return 1;
            }
            else if (this.situacao == 'f' && h.situacao== 'i'){
                return -1;
            }
        }
        return 0;
    } 
}