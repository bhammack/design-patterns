public class Test
{
    public static void main(String[] args)
    {
        Currency myBankAccount = new Currency(3.50000000);
        Currency fortKnox = new Currency(180e9);
        Currency treeFiddy = new Currency(3.5);
        CartPoint pointThreeFour = new CartPoint(3.0000, 4.0000);
        CartPoint pointZeroZero = new CartPoint(0, 0);
        CartPoint pointNegFourNegThree = new CartPoint(-4, -3);
        // test the currencies and cartpoints.
        System.out.print("<=====< Testing Unequal Currencies >=====>\n");
        testMagnitude(myBankAccount, fortKnox);
        System.out.print("<=====< Testing Equal Currencies >=====>\n");
        testMagnitude(treeFiddy, myBankAccount);        
        System.out.print("<=====< Testing Unequal CartPoints >=====>\n");
        testMagnitude(pointThreeFour, pointZeroZero);
        System.out.print("<=====< Testing Equal CartPoints >=====>\n");
        testMagnitude(pointNegFourNegThree, pointThreeFour);
    }

    public static void testMagnitude(Magnitude m1, Magnitude m2)
    {
        System.out.print("m1  <   m2: " + m1.lessThan(m2) + "\n");
        System.out.print("m1  <=  m2: " + m1.lessThanEqualTo(m2) + "\n");
        System.out.print("m1  =   m2: " + m1.equalTo(m2) + "\n");
        System.out.print("m1  >=  m2: " + m1.greaterThanEqualTo(m2) + "\n");
        System.out.print("m1  >   m2: " + m1.greaterThan(m2) + "\n");
        System.out.print("m1  !=  m2: " + m1.notEqual(m2) + "\n");
    }
}