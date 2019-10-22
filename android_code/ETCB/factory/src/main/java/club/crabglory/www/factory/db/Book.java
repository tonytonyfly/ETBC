package club.crabglory.www.factory.db;

public class Book {

    private int image;
    private String name;
    private String description;
    private String upper;
    private String price;

    public void setImage(int image) {
        this.image = image;
    }
    public void setName(String resourceName) {
        this.name = resourceName;
    }
    public int getImage() { return image; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUpper() { return upper; }
    public void setUpper(String upper) { this.upper = upper; }
    public void setPrice(double price) { this.price = price + " $"; }
    public String getPrice() { return price; }
}
