import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.util.ArrayList;
import java.io.*;

public class CSVTools {

    ArrayList<String> instance;
    String nameFile;
    private ArrayList<HospitalAgent> hospital=new ArrayList<HospitalAgent>();
    private ArrayList<BedAgent> bed = new ArrayList<BedAgent>();
    private ArrayList<PatientAgent> patient = new ArrayList<PatientAgent>();
    private Conf conf;


    public CSVTools(String XFile, Conf xConf) throws IOException, CsvValidationException {
            this.instance= new ArrayList<String>();
            this.conf=xConf;
            this.readDataLineByLine(XFile);
    }


    public ArrayList<HospitalAgent> getHospital(){
        return this.hospital;
    }

    public ArrayList<BedAgent> getBedAgent(){
        return this.bed;
    }

    public ArrayList<PatientAgent> getPatient() {
        return this.patient;
    }

    // Java code to illustrate reading a
// CSV file line by line
    public  void readDataLineByLine(String xFile) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new FileReader(xFile));
        String[] record = null;
        Boolean first=true;
        while ((record = reader.readNext()) != null) {
            if (first){
                first=false;
            }
            else{
                //System.out.println("Type of Agent: "+record[0]+" Id of Agent: "+record[1]+" Day of Arrival: "+record[2]+" BedOrPatientType: "+record[3]+"CloserHospital: "+record[4]+" :LOS: "+record[5]);
                if (record[0].equals("0"))  // Is an Hospital
                {
                    int idAgent=Integer.parseInt(record[1]);
                    int timeArrival=Integer.parseInt(record[2]);
                    HospitalAgent HA=new HospitalAgent(idAgent,timeArrival,this.conf);
                    HA.setType(0);
                    this.hospital.add(HA);
                }
                else if (record[0].equals("1")){
                    int idAgent=Integer.parseInt(record[1]);
                    int timeArrival=Integer.parseInt(record[2]);
                    int typeOfBed=Integer.parseInt(record[3]);
                    int CloserHospital=Integer.parseInt(record[4]);
                    BedAgent BA=new BedAgent(idAgent,timeArrival,this.conf);
                    BA.setType(1);
                    BA.setTypeBed(typeOfBed);
                    BA.setHospitalOfAllocation(CloserHospital);
                    this.bed.add(BA);
                }
                else {  // record[0]=="2"
                    int idAgent=Integer.parseInt(record[1]);
                    int timeArrival=Integer.parseInt(record[2]);
                    int typeOfPatient=0;
                    Boolean type=Boolean.parseBoolean(record[3]);
                    int CloserHospital=Integer.parseInt(record[4]);
                    int dayOfDeparture=Integer.parseInt(record[5]);
                    PatientAgent PA=new PatientAgent(idAgent,timeArrival,true,this.conf);
                    PA.setType(2);
                    PA.setArrivalDay(timeArrival);
                    if (type.equals(Boolean.TRUE)){
                        PA.setVentilationSupport();
                    }
                    PA.setCloserHospital(CloserHospital);
                    PA.setDepartureDay(dayOfDeparture);
                    this.patient.add(PA);
                }
            }

        }

        reader.close();
        System.out.println("Number of Hospital agents:"+this.hospital.size());
        System.out.println("Number of Bed agents:"+this.bed.size());
        System.out.println("Number of Patient agents:"+this.patient.size());
    }

}
