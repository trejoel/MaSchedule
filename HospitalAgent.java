public class HospitalAgent extends Agent implements Runnable {

	private Conf conf;

	public HospitalAgent(int id,int timeStep, Conf xConf) {
		super(id,timeStep,xConf);
		//conf=new ExperimentRunConfiguration(20,1,3,100, 500);
		conf=xConf;
		// TODO Auto-generated constructor stub
	}


	// Concurrent execution

	public void run(){
	   act();
	}



	@Override
	public void act() {
		// TODO Auto-generated method stub
		System.out.println("Hospital");
	}

}
