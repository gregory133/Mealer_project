public class Client extends User{

    private CreditCardInformation payment;

    public Client(String f, String l, String a, String e, String p, CreditCardInformation payment1){
        firstName = f;
        lastName = l;
        address = a;
        email = e;
        password = p;
        payment = payment1;
    }
}
