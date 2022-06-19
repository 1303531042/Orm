package domain;

public class Car {
    private Integer cno;
    private String cname;
    private String color;
    private Integer price;

    @Override
    public String toString() {
        return "Car{" +
                "cno=" + cno +
                ", cname='" + cname + '\'' +
                ", color='" + color + '\'' +
                ", price=" + price +
                '}';
    }

    public Car() {
    }

    public Car(Integer cno, String cname, String color, Integer price) {
        this.cno = cno;
        this.cname = cname;
        this.color = color;
        this.price = price;
    }
}
