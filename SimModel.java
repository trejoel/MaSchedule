import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileWriter;



public class SimModel {
	private Scheduler scheduler;
	private Random rand;
	private Conf conf;
	private ArrayList<HospitalAgent> hospital=new ArrayList<HospitalAgent>();
	private ArrayList<BedAgent> bed = new ArrayList<BedAgent>();
	private ArrayList<PatientAgent> patient = new ArrayList<PatientAgent>();
	private ArrayList<Agent> agents = new ArrayList<>();


	SimModel(Conf xConf) {
		// TODO Auto-generated constructor stub
		conf=xConf;
		scheduler = new Scheduler(conf);
		rand=new Random(System.currentTimeMillis());

	}





	//generate random instances



	//update the start so that we include first hospital agents, then bed agents and later patient agents. According to list of agents

	public void start() throws CsvValidationException, IOException {
		// TODO Auto-generated method stub
		SimModel model=new SimModel(this.conf);
		CSVTools ut=new CSVTools("Instance2024_09_30.csv",this.conf);
		this.hospital=ut.getHospital();
		for (int i=0; i<hospital.size(); i++) {
			model.scheduler.add(hospital.get(i));
		}
		this.bed=ut.getBedAgent();
		for (int i=0; i<bed.size(); i++) {
			model.scheduler.add(bed.get(i));
		}
		this.patient=ut.getPatient();
		for (int i=0; i<patient.size(); i++) {
			model.scheduler.add(patient.get(i));
		}
		model.scheduler.execute(0);
	}




	public void generate(Conf conf)  {
		// TODO Auto-generated method stub
		try {
			//FileWriter xFile = new FileWriter("Test.csv");
			FileWriter xFile = new FileWriter(conf.getInstance());
			CSVWriter writer = new CSVWriter(xFile);
			String[] header;
			header= new String[]{"Type", "Id", "Day", "BedorPatientType", "CloserHospital", "LOS"};
			writer.writeNext(header);
			Random ran = new Random();
			int newDay=0;
			SimModel model=new SimModel(conf);
			Agent hospital =  new HospitalAgent(100,0, this.conf);
			String[] row;
			row=new String[]{String.valueOf(hospital.getType()),String.valueOf(hospital.getId()),String.valueOf(0),"NA",String.valueOf(hospital.getId()),String.valueOf(conf.getNumberOfDays())};
			writer.writeNext(row);
			hospital.setType(3);
			model.scheduler.add(hospital);
			//Agent patient = new PatientAgent(200,0);
			//model.scheduler.addLast(patient);
			ArrayList<Agent> beds = new ArrayList<Agent>();
			int xTime=0;
			int number_of_beds=100;
			for(int i=0; i<conf.getNumberOfBeds(); i++) {
				if (xTime<=conf.getNumberOfDays()){
					Agent bed = new BedAgent(i,xTime, this.conf);
					bed.setType(1);
					int typeOfBed=ran.nextInt()%10;
					int bedType;
					if (typeOfBed<7){
						bedType=0;
					}
					else{
						bedType=1;
					}
					((BedAgent)bed).setTypeBed(bedType);
					int closerHospital=ran.nextInt()%3;
					if (closerHospital<0){
						closerHospital=closerHospital*-1;
					}
					((BedAgent)bed).setHospitalOfAllocation(closerHospital);
					//{"Type","Id","Day","BedType","CloserHospital","LOS"};
					model.scheduler.add(bed);
					beds.add(bed);
					row=new String[]{"1",String.valueOf(i),String.valueOf(xTime),String.valueOf(bedType),String.valueOf(((BedAgent) bed).getHospitalOfAllocation()),String.valueOf(conf.getNumberOfDays())};
					writer.writeNext(row);
				}
			}
			ArrayList<Agent> patients = new ArrayList<Agent>();
			xTime=0;
			int depDay=0;
			int distribution=conf.getNumberOfPatients()/conf.getNumberOfDays();
			newDay=0;
			for(int i=0; i<conf.getNumberOfPatients(); i++) {
				//generate a random number to determine if it is a new day
				newDay=newDay+1;
				//if (i % 9 ==0 ){
				if (newDay>=distribution){
					newDay=0;
					if (xTime<(conf.getNumberOfDays()-1)) {
						xTime++;
						Agent patient = new PatientAgent(i, xTime, true,this.conf);
						patient.setType(2);
						((PatientAgent) patient).setArrivalDay(xTime);
						depDay=xTime + ran.nextInt(10);
						if (depDay>=conf.getNumberOfDays()){
							depDay=conf.getNumberOfDays()-1;
						}
						((PatientAgent) patient).setDepartureDay(depDay);
						if (ran.nextInt(10)>7){
							((PatientAgent) patient).setVentilationSupport();
						}
						//patient.act();
						model.scheduler.add(patient);
						patients.add(patient);
						//{"Type","Id","Day","BedType","CloserHospital","LOS"};
						row=new String[]{"2",String.valueOf(i),String.valueOf(((PatientAgent) patient).getArrivalDay()),String.valueOf(((PatientAgent) patient).getRequiredVentilation()),String.valueOf(((PatientAgent) patient).getCloserHospital()),String.valueOf(((PatientAgent) patient).getDepartureDay())};
						writer.writeNext(row);
					}
				}
				else{
					if (xTime<conf.getNumberOfDays()) {
						Agent patient = new PatientAgent(i, xTime, true, this.conf);
						patient.setType(2);
						((PatientAgent) patient).setArrivalDay(xTime);
						depDay=xTime + ran.nextInt(10);
						if (depDay>=conf.getNumberOfDays()){
							depDay=conf.getNumberOfDays()-1;
						}
						((PatientAgent) patient).setDepartureDay(depDay);
						if (ran.nextInt(10)>7){
							((PatientAgent) patient).setVentilationSupport();
						}
						//patient.act();
						model.scheduler.add(patient);
						patients.add(patient);
						//{"Type","Id","Day","BedType","CloserHospital","LOS"};
						row=new String[]{"2",String.valueOf(i),String.valueOf(((PatientAgent) patient).getArrivalDay()),String.valueOf(((PatientAgent) patient).getRequiredVentilation()),String.valueOf(((PatientAgent) patient).getCloserHospital()),String.valueOf(((PatientAgent) patient).getDepartureDay())};
						writer.writeNext(row);
					}
				}
			}
			int numSteps=0;
			int simulationSteps=11;
			model.scheduler.execute(0);
			writer.close();
		}
		catch (IOException e){
			System.out.println("Error creating file");
			e.printStackTrace();
		}

	}

}
