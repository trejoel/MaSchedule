import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Scheduler {
	private ArrayList<Agent> queue = new ArrayList<Agent>();
	private Agent last=null, first=null;
	private long steps;
	private Conf conf;
	private ArrayList<BedAgent> setNormalBeds;
	private ArrayList<BedAgent> setVentBeds;
	private int unAssignedPatients;
	private int AssignedPatients;
	private int CorrectHospital;
	private int WrongHospital;

	Scheduler(Conf xConf) {  //Metrics
		steps=0;
		conf=xConf;
		unAssignedPatients=0;
		AssignedPatients=0;
		CorrectHospital=0;
		WrongHospital=0;
	}
	
	public void add(Agent agent) {
		queue.add(agent);
	}
	
	public void addLast(Agent agent) {
		last=agent;
	}
	
	public void addFirst(Agent agent) {
		first=agent;
	}

	public int getNumberOfNonAllocated(){
		return this.unAssignedPatients;
	}

	public int getNumberOfAllocated(){
		return AssignedPatients;
	}


	public int getNumberCorrectdHospital(){
		return CorrectHospital;
	}

	public int getNumberWrongHospital(){
		return WrongHospital;
	}



	public ArrayList<BedAgent> getListOfBeds(int Type){
		ArrayList<BedAgent> setBeds=new ArrayList<BedAgent>();
		Agent A;
		int i=0;
		while (i<queue.size()){
			A=queue.get(i);
			if (A.getType()==1){
				if (Type==0){
					if (((BedAgent)A).getTypeBed()==0){
						setBeds.add((BedAgent) A);
					}
				}
				else{
					if (((BedAgent)A).getTypeBed()==1){
						setBeds.add((BedAgent) A);
					}
				}
			}
			i++;
		}
		return setBeds;
	}


	// Indicate that unassigned patients during the day of arrival remains unassigned since they are critical patients
	public void execute(int day) { //day is the step of simulation
		setNormalBeds=this.getListOfBeds(0);
		setVentBeds= this.getListOfBeds(1);
		ArrayList<Agent> partialAgentList=new ArrayList<Agent>();
		ArrayList<Agent> partialPatientList=new ArrayList<Agent>();
		ArrayList<PatientAgent> auxPatientAgentList=new ArrayList<PatientAgent>();
		ArrayList<Agent> auxNonPatientAgentList=new ArrayList<Agent>();


		int i=0;
		int curDay=0;
		int prevDay=0;
		int type=0;
		Thread thread;
		Agent A;
		PatientAgent P;
		BedAgent B;
		HospitalAgent H;
		int posArrayNormalBeds=0;
		int posArrayVentBeds=0;
		int xPosNArray=0; //-1 if it is unassigned
		int xPosVArray=0; //-1 if it is unassigned
		for (int xDay=0;xDay<conf.getNumberOfDays();xDay++){
			//An alternative is the second parameter to be prevXday instead of pos_simulation. It requires the queue is sorted by day
			partialAgentList=getAgentsByDay(xDay);
			auxPatientAgentList=getPatientsByDay(partialAgentList);
			auxNonPatientAgentList=getNonPatientsByDay(partialAgentList);
			if (conf.getPolicyOfAssignment()==1)//Round robin
			{
				partialAgentList.clear();
				partialAgentList.addAll(auxNonPatientAgentList);
				for (int ll=0;ll<auxPatientAgentList.size();ll++){
					A=(Agent)auxPatientAgentList.get(ll);
					partialAgentList.add(A);
				}
			}
			if (conf.getPolicyOfAssignment()==2){ // Long Stay first
				partialAgentList.clear();
				partialAgentList.addAll(auxNonPatientAgentList);
				auxPatientAgentList=this.bestFit(auxPatientAgentList);
				partialAgentList.addAll(auxPatientAgentList);
			}
			if (conf.getPolicyOfAssignment()==3){  //Short stay first
				partialAgentList.clear();
				partialAgentList.addAll(auxNonPatientAgentList);
				auxPatientAgentList=this.worstFit(auxPatientAgentList);
				partialAgentList.addAll(auxPatientAgentList);
			}
			i=0;
			if (partialAgentList.size()==0){
				System.out.println("No agents register the day "+xDay);
			}
			while (i<partialAgentList.size()){
				A=(Agent) (partialAgentList.get(i));
				type=A.getType();
				if (type==2){
					P=(PatientAgent) (A);
					curDay=P.getTime();
					thread = new Thread((Runnable) P);
					if (i==0){
						P.setDelay(10);
					}
					thread.start();
					prevDay=curDay;
					if (((PatientAgent)P).getRequiredVentilation()){
						xPosVArray=assignBed(P,conf.getPolicyOfAssignment(),posArrayNormalBeds,posArrayVentBeds);
						posArrayVentBeds=xPosVArray;
						System.out.println("PosArrayVent: " +posArrayVentBeds);
					}
					else{
						xPosNArray=assignBed(P,conf.getPolicyOfAssignment(),posArrayNormalBeds,posArrayVentBeds);
						posArrayNormalBeds=xPosNArray;
						System.out.println("PosArrayNorm: "+posArrayNormalBeds);

					}
				}
				else if (type==1) { //This is a bed
					B=(BedAgent) (A);
					thread = new Thread((Runnable) B);
					thread.start();
				}
				else {
					H=(HospitalAgent) (A);
					thread = new Thread((Runnable) H);
					thread.start();
				}
				i++;
			}
		}
		try {
			String filename="outputInstance";
			switch (conf.getPolicyOfAssignment()){
				case 1: filename=filename+"roundrobin.csv";
						break;
				case 2: filename=filename+"bestFit.csv";
						break;
				case 3: filename=filename+"worstFit.csv";
						break;
				case 4: filename=filename+"FirstFit.csv";
						break;
			}
			FileWriter xFile = new FileWriter(filename);
			CSVWriter writer = new CSVWriter(xFile);
			String[] header;
			header= new String[]{"Heuristic","Assigned Patients", "Non-Allocated Patients", "Allocated to closer hospital", "Allocated to wrong hospital"};
			writer.writeNext(header);
			String[] row;
			row=new String[]{String.valueOf(conf.getPolicyOfAssignment()),String.valueOf(this.getNumberOfAllocated()),String.valueOf(getNumberOfNonAllocated()),String.valueOf(getNumberCorrectdHospital()), String.valueOf(getNumberWrongHospital())};
			writer.writeNext(row);
			writer.close();
		}
		catch (IOException e){
			System.out.println("Error creating file");
			e.printStackTrace();
		}
		System.out.println("Assigned Patients:"+this.getNumberOfAllocated()+" and number of No Allocated Patients:"+getNumberOfNonAllocated());
		System.out.println("Number of Patients Allocated to close hospital:"+getNumberCorrectdHospital());
		System.out.println("Number of Patients Allocated to wrong hospital:"+getNumberWrongHospital());
	}


	private ArrayList<PatientAgent> worstFit(ArrayList<PatientAgent> list){
		int n=list.size();
		for (int i=0;i<n-1;i++){
			int min_idx=i;
			for (int j=i+1; j<n; j++){
			  if (list.get(j).getDepartureDay()<list.get(min_idx).getDepartureDay()){
				min_idx=j;
			  }
			  PatientAgent p_min=list.get(min_idx);
			  PatientAgent p_i=list.get(i);
			  list.set(min_idx,p_i);
			  list.set(i,p_min);
		  }
		}
		return list;
	}


	private ArrayList<PatientAgent> bestFit(ArrayList<PatientAgent> list){
		int n=list.size();
		for (int i=0;i<n-1;i++){
			int min_idx=i;
			for (int j=i+1; j<n; j++){
				if (list.get(j).getDepartureDay()>list.get(min_idx).getDepartureDay()){
					min_idx=j;
				}
				PatientAgent p_min=list.get(min_idx);
				PatientAgent p_i=list.get(i);
				list.set(min_idx,p_i);
				list.set(i,p_min);
			}
		}
		return list;
	}

	private ArrayList<PatientAgent> sortAgents(int longStay, ArrayList<PatientAgent> list){
		int n=list.size();
		for (int i=0;i<n-1;i++){
			int min_idx=i;
			for (int j=i+1; j<n; j++){
				if (longStay==1){
					if (list.get(j).getDepartureDay()>list.get(min_idx).getDepartureDay()){
						min_idx=j;
					}
				}else{ //longStay==0
					if (list.get(j).getDepartureDay()<list.get(min_idx).getDepartureDay()){
						min_idx=j;
					}
				}
				PatientAgent p_min=list.get(min_idx);
				PatientAgent p_i=list.get(i);
				list.set(min_idx,p_i);
				list.set(i,p_min);
			}
		}
		return list;
	}



	private ArrayList<Agent> getAgentsByDay(int day){
		ArrayList<Agent> partialAgentList=new ArrayList<Agent>();
		int i=0;
		Agent A;
		while (i<queue.size()){
			A=(Agent) (queue.get(i));
			//System.out.println("Agent at day "+A.getTime());
			if (A.getTime()==day){
				partialAgentList.add(A);
			}
			i++;
		}
		return partialAgentList;
	}

	private ArrayList<PatientAgent> getPatientsByDay(ArrayList<Agent> list){
		Agent A;
		ArrayList<PatientAgent> auxPatientAgentList=new ArrayList<PatientAgent>();
		for (int i=0;i<list.size();i++){
			A=(Agent)list.get(i);
			if (A.getType()==2){
				auxPatientAgentList.add((PatientAgent)A);
			}
		}
		return auxPatientAgentList;
	}


	private ArrayList<Agent> getNonPatientsByDay(ArrayList<Agent> list){
		Agent A;
		ArrayList<Agent> auxNonPatientAgentList=new ArrayList<Agent>();
		for (int i=0;i<list.size();i++){
			A=(Agent)list.get(i);
			if (A.getType()!=2){
				auxNonPatientAgentList.add(A);
			}
		}
		return auxNonPatientAgentList;
	}

	private int switch2BestFit(ArrayList<Agent> listAgents,int k){
		Agent A;
		int maxNumberDays=0;
		int index=k;
		for (int i=k; i<listAgents.size();i++){
			A=(Agent) listAgents.get(i);
			if (A.getType()==2){
				PatientAgent P=(PatientAgent)A;
				if (P.getDepartureDay()>maxNumberDays){
					maxNumberDays=P.getDepartureDay();
					index=i;
				}
			}
		}
		return index;
	}



	protected int assignBed(Agent xPatient,int xPolicy,int posArrayNormal,int posArrayVent){
		int assigned=-1; //-1 is assigned is not assigned
		assigned=roundRobin(xPatient,posArrayNormal,posArrayVent);
		if (assigned>=0){
			if (((PatientAgent)xPatient).getRequiredVentilation()){
				System.out.println("Patient "+xPatient.getId()+" whose close hospital is:"+((PatientAgent) xPatient).getCloserHospital()+" assigned to bed "+(setVentBeds.get(assigned)).getId()+ " From HOSPITAL:"+(setVentBeds.get(assigned)).getHospitalOfAllocation()+" at day: "+((PatientAgent)xPatient).getArrivalDay());
				if (((PatientAgent) xPatient).getCloserHospital()==(setVentBeds.get(assigned)).getHospitalOfAllocation()){
					this.CorrectHospital++;
				}
				else{
					this.WrongHospital++;
				}
			}
			else{
				System.out.println("Patient "+xPatient.getId()+" whose close hospital is:"+((PatientAgent) xPatient).getCloserHospital()+" assigned to bed "+(setNormalBeds.get(assigned)).getId()+ " From HOSPITAL:"+(setNormalBeds.get(assigned)).getHospitalOfAllocation()+" at day: "+((PatientAgent)xPatient).getArrivalDay());
				if (((PatientAgent) xPatient).getCloserHospital()==(setNormalBeds.get(assigned)).getHospitalOfAllocation()){
					this.CorrectHospital++;
				}
				else{
					this.WrongHospital++;
				}
			}
			this.AssignedPatients++;
		}else{
			this.unAssignedPatients++;
			System.out.println("No bed available for patient "+xPatient.getId()+" at day: "+((PatientAgent)xPatient).getArrivalDay());
		}
		return assigned;
	}

protected int firstFit(Agent xPatient, int posArrayNormal, int posArrayVentilation){
	BedAgent xBed;
	int assigned=-1; //-1 if it iss not assigned
	int i=0;
	int xPosArrayNormal;
	int xPosArrayVentilation;
	if (posArrayNormal>0){
		xPosArrayNormal=posArrayNormal;
	}
	else{
		xPosArrayNormal=0;
	}
	if (posArrayVentilation>0){
		xPosArrayVentilation=posArrayVentilation;
	}
	else{
		xPosArrayVentilation=0;
	}
	if (((PatientAgent)xPatient).getRequiredVentilation()){
		while (i<setVentBeds.size()){
			xBed=setVentBeds.get(xPosArrayVentilation);
			if (xBed.isAvailable(((PatientAgent)xPatient).getArrivalDay(),((PatientAgent)xPatient).getDepartureDay())){
				xBed.allocatePatient(((PatientAgent)xPatient));
				i=setVentBeds.size();
				return xPosArrayVentilation;
			}
			else{
				xPosArrayVentilation=((xPosArrayVentilation+1)%setVentBeds.size());
				i++;
			}
		}
	}
	else{
		while (i<setNormalBeds.size()){
			xBed=setNormalBeds.get(xPosArrayNormal);
			if (xBed.isAvailable(((PatientAgent)xPatient).getArrivalDay(),((PatientAgent)xPatient).getDepartureDay())){
				xBed.allocatePatient(((PatientAgent)xPatient));
				i=setNormalBeds.size();
				return xPosArrayNormal;
			}
			else{
				xPosArrayNormal=((xPosArrayNormal+1)%setNormalBeds.size());
				i++;
			}
		}
	}

	return -1;
}

  protected int roundRobin(Agent xPatient,int posArrayNormal, int posArrayVentilation){
		BedAgent xBed;
		int assigned=-1; //-1 if it si not assigned
	  	int i=0;
	    int xPosArrayNormal;
		int xPosArrayVentilation;
	    if (posArrayNormal>0){
			xPosArrayNormal=posArrayNormal;
		}
		else{
			xPosArrayNormal=0;
		}
	  if (posArrayVentilation>0){
		  xPosArrayVentilation=posArrayVentilation;
	  }
	  else{
		  xPosArrayVentilation=0;
	  }
	  if (((PatientAgent)xPatient).getRequiredVentilation()){
		  int bestVentilationBed=-1;
		  while (i<setVentBeds.size()){
			  //System.out.println("Ith"+i+" From "+setVentBeds.size());
			  xBed=setVentBeds.get(xPosArrayVentilation);
			  if (xBed.isAvailable(((PatientAgent)xPatient).getArrivalDay(),((PatientAgent)xPatient).getDepartureDay())){
				  xBed.allocatePatient(((PatientAgent)xPatient));
				  i=setVentBeds.size();
				  return xPosArrayVentilation;
			  }
			  else{
				  xPosArrayVentilation=((xPosArrayVentilation+1)%setVentBeds.size());
				  i++;
			  }
		  }
	  }
	  else{
		  while (i<setNormalBeds.size()){
			  xBed=setNormalBeds.get(xPosArrayNormal);
			  if (xBed.isAvailable(((PatientAgent)xPatient).getArrivalDay(),((PatientAgent)xPatient).getDepartureDay())){
				  xBed.allocatePatient(((PatientAgent)xPatient));
				  i=setNormalBeds.size();
				  return xPosArrayNormal;
			  }
			  else{
				  xPosArrayNormal=((xPosArrayNormal+1)%setNormalBeds.size());
				  i++;
			  }
		  }
	  }

		return -1;
  }

	public void reset() {
		queue.clear();
	}

	public long getSteps() {
		return steps;
	}

	
}
