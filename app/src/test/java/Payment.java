public class Payment {

    private int cardNumber;
    private int cvv;
    private int expiryDateMonth;
    private int expiryDateYear;
    private String cardHolderName;

    public Payment(int cN, int cvv, int eDM, int eDY, String cHN){
        cardNumber = cN;
        this.cvv = cvv;
        expiryDateMonth = eDM;
        expiryDateYear = eDY;
        cardHolderName = cHN;
    }

}
