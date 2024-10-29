import java.util.Random;


public class BedAgent extends Agent implements Runnable{

	private int typeBed; // A bed is of type 0 (normal) 1 means (ventilation)
	private boolean[] available; // Array of available days.  1 is available, 0 is not available
	private Conf conf;
	private int hospitalOfAllocation; // from the set ExperimentRunConfiguration {1,..,numHospitals}

	public BedAgent(int id,int timeStep, Conf xConf) {
		super(id,timeStep,xConf);
		Random rand=new Random();
		conf=xConf;
		//conf=new ExperimentRunConfiguration(20,1,3,100, 500);
		this.available= new boolean[xConf.getNumberOfDays()];
		for (int i=0;i<conf.getNumberOfDays();i++) {
			this.available[i]=true;
		}
		this.hospitalOfAllocation=rand.nextInt(conf.getNumberOfHospitals())+1 ;
		// TODO Auto-generated constructor stub
	}

	public void setTypeBed(int xType){
		this.typeBed=xType;
	}

	public int getTypeBed(){
		return this.typeBed;
	}

	public boolean getAvailable(int xDay){
		return this.available[xDay];
	}

	public void setHospitalOfAllocation(int xHospital){
		this.hospitalOfAllocation=xHospital;
	}

	public int getHospitalOfAllocation(){
		return this.hospitalOfAllocation;
	}

	public void makeAvailable(int xDay){
		this.available[xDay]=true;
	}

	public void makeUnavailable(int xDay){
		this.available[xDay]=false;
	}
	//Concurrent Execution


	public boolean isAvailable(int fromDay, int toDay){
		boolean isAv=true;
		if (toDay>conf.getNumberOfDays()){
			toDay=conf.getNumberOfDays();
		}
		System.out.println("fromDay:"+fromDay+"ToDay"+toDay);
		for (int i=fromDay; i<=toDay;i++){
			if (!this.available[i]){
				isAv=false;
			}
		}
		return isAv;
	}


	public void allocatePatient(PatientAgent p) {
		int xDay=p.getArrivalDay();
		int numberOfDays = computeNumberOfDays(p, p.getArrivalDay());
		boolean assigned=true;
		for (int i = 0; i <= numberOfDays; i++) {
			if (xDay<=conf.getNumberOfDays()){  //Avoiding to allocate a patient in a day greater than the simulation horizonts
				if (this.available[xDay]) {
					this.available[xDay] = false;
				}
				else{
					// It is sufficient to make unavailable one single day to do not allocate
					assigned=false;
				}
				xDay++;
			}
		}
		if (assigned){
			p.allocate();
		}
	}


	protected int computeNumberOfDays(PatientAgent p, int xDay){
		if (p.getDepartureDay()>=xDay){
			return p.getDepartureDay()-xDay;
		}
		return 0;
	}

	public void run(){
		act();
	}

	@Override
	public void act() {
		// TODO Auto-generated method stub
		System.out.println("Bed ID = "+getId()+" available at day "+this.getTime()+" at Hospital:"+this.hospitalOfAllocation+"\n");
	}

}
