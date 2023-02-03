package Objects;

public class TextModifier {

    String text;

    public TextModifier() {
    }

    public TextModifier(String text) {
        this.text = text;
    }

    public void setSentenceCase(String text){
        this.text = text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public String getSentenceCase(){
        return text;
    }

    public void setAllCaps(String text){
        this.text = text.toUpperCase();
    }

    public String getAllCaps(){
        return text;
    }

    public void setAllLowerCase(String text){
        this.text = text.toLowerCase();
    }

    public String getAllLowerCase(){
        return text;
    }
}
