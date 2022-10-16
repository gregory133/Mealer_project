/**
 * implements the Singleton pattern
 */
public class ProjectSettings {

    private ProjectSettings instance;

    private String[] admins;
    private String[] mealTypes;
    private String cuisineTypes;


    private ProjectSettings(){}


    public ProjectSettings getInstance(){
        if (instance==null){
            instance=new ProjectSettings();
        }
        return instance;
    }
}
