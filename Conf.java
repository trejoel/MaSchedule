public class Conf {
    private int numberOfDays;
    private int policyOfAssignment; // 1 Round robin; 2 Best Fit (largest Number of Stay); 3 Worst Fit (smaller Number of days to stay); 4 First Fit (Fist to hold a bias in the number of days)
    private int numberOfHospitals; // by Default is just one hospital
    private int numberOfBeds;
    private int numberOfPatients;
    private String instance;
    private Boolean haveInstance;



    public Conf(int xnumberOfDays, int xpolicyOfAssignment, int XnumberOfHospitals, int XnumberBeds, int XnumberOfPatients, String xInstance) {
        this.numberOfDays = xnumberOfDays;
        this.policyOfAssignment=xpolicyOfAssignment;
        this.numberOfHospitals=XnumberOfHospitals;
        this.numberOfBeds=XnumberBeds;
        this.numberOfPatients=XnumberOfPatients;
        this.instance=xInstance;
        this.haveInstance=containsInstance();
    }


    public void setNumberOfHospitals(int xNH){
        this.numberOfHospitals=xNH;
    }

    public int getNumberOfDays() {
        return this.numberOfDays;
    }

    public String getInstance(){
        return this.instance;
    }

    public int getPolicyOfAssignment(){
        return this.policyOfAssignment;
    }

    public int getNumberOfHospitals(){return this.numberOfHospitals; }

    public int getNumberOfBeds(){return this.numberOfBeds;  }

    public int getNumberOfPatients(){return this.numberOfPatients; }

    public Boolean containsInstance(){
        if (this.instance.isEmpty()){
            return false;
        }
        return true;
    }

}