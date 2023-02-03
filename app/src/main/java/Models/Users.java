package Models;

public class Users {

    private String usersId, fname, lname, contactNum, email, password, imageUrl, imageName;
    private long rating;
    private boolean hasSellerAccount;
    private boolean sellerMode;

    public Users() {
    }

    public Users(String usersId, String fname, String lname, String contactNum, String email,
                 String password, String imageUrl, String imageName, long rating,
                 boolean hasSellerAccount, boolean sellerMode) {

        this.usersId = usersId;
        this.fname = fname;
        this.lname = lname;
        this.contactNum = contactNum;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.rating = rating;
        this.hasSellerAccount = hasSellerAccount;
        this.sellerMode = sellerMode;
    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public boolean isHasSellerAccount() {
        return hasSellerAccount;
    }

    public void setHasSellerAccount(boolean hasSellerAccount) {
        this.hasSellerAccount = hasSellerAccount;
    }

    public boolean isSellerMode() {
        return sellerMode;
    }

    public void setSellerMode(boolean sellerMode) {
        this.sellerMode = sellerMode;
    }
}
