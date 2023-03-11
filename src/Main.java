public class Main {
    public static void main(String[] args) {
        TimedConcurrentHashMap<String,String> map = new TimedConcurrentHashMap<>();
        map.put("Key","Value");
        System.out.println("Hello world!");
        System.out.println(map.getElapsedTime("Key"));
    }
}