package io.github.tomasevicjovan.qacommercelab.data;

/** A published test card. The number is printed the way it is on the card, with spaces. */
public record TestCard(String brand, String brandName, String number) {

    public String digits() {
        return number.replace(" ", "");
    }

    public String last4() {
        return digits().substring(digits().length() - 4);
    }

    public String cvc() {
        return "amex".equals(brand) ? "1234" : "123";
    }
}
