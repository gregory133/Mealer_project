public class CreditCardInformation {

    private int cardNumber;
    private int cvv;
    private int expMonth;
    private int expYear;
    private String cardHolderName;

    public CreditCardInformation(int cN, int cvv, int eDM, int eDY, String cHN){
        cardNumber = cN;
        this.cvv = cvv;
        expMonth = eDM;
        expYear = eDY;
        cardHolderName = cHN;
    }

}
