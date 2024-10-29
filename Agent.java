

//Include for each agent a day of arrival and a day of remove. At the scheduler day by day perform a mechanism to include and remove daily all the agents needed to be removed

public abstract class Agent {
	private int id;
	private int time; // This represents the time of arrival of the agent. Each time step represents a day
	private int type; //1-bed, 2- patient, 3 hospital
	private Conf conf;

	
	public Agent(int id, int timeStep, Conf xConf) {
		this.conf = xConf;
		this.setId(id);
		this.setTime(timeStep);
		conf=new Conf(20,1,3,100, 500);

	}

	public abstract void act();
	
	public int getId() {
		return id;
	}
	public int getType(){return type;}
	public void setId(int id) {
		this.id = id;
	}
	public void setTime(int xTime){this.time=xTime;}
	public int getTime(){return this.time;}
	public void setType(int _type){
		this.type=_type;
	}
		
}
