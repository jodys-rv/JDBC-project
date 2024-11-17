package projects;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;
/**
 * user interface for the projects database
 */
public class ProjectsApp {
  //@formatter:off
  private List<String> options = List.of(
      "1) Add a project",
      "2) List projects",
      "3) Select a project",
      "4) Update project details",
      "5) Delete a project",
      "6) Add category to project",
      "7) Add material to project"
      
      );
  // @formatter:on
  private Scanner scanner = new Scanner(System.in);
  ProjectService projectService = new ProjectService();
  Project curProject;
  
  public static void main(String[] args) {
    new ProjectsApp().processUserSelections();
  }
  
  /**
   * provides the switch statement for the menu
   */
  private void processUserSelections() {
    boolean done = false;
    while (!done) {
      try {
        int selection = getUserSelection();
        switch(selection) {
          case -1:
            done = true;
            System.out.println("Exiting menu.");
            break;
          case 1:
            createProject();
            break;            
          case 2:
            listProjects();
            break;            
          case 3:
            selectProject();
            break;            
          case 4:
            updateProjectDetails();
            break;            
          case 5:
            deleteProject();
            break;
          case 6: 
            addCategoryToProject();
            break;
          case 7:
            addMaterialToProject();
            break;
          
          default:
            System.out.println("\n" + selection + " is not a valid selection.");
        }
      } catch (Exception e) {
        System.out.println("\nError: " + e + " Please try again.");
      }
    } 
  }
  /**
   * collects input from user to add a material to a project
   */
  private void addMaterialToProject() {
    if(Objects.isNull(curProject)) {
      System.out.println("\nPlease select a project");
      return;
    }
    String materialName = getStringInput("Enter the name of the material");
    Integer numRequired = getIntInput("Enter the number needed for the project");
    BigDecimal cost = getDecimalInput("Enter the cost per unit");
    Material material = new Material();
    Integer projectId = curProject.getProjectId();
    material.setProjectId(projectId);
    material.setMaterialName(materialName);
    material.setNumRequired(numRequired);
    material.setCost(cost);
    projectService.addMaterialToProject(curProject.getProjectId(), material);
    curProject = projectService.fetchProjectById(curProject.getProjectId());
    System.out.println("You have entered the material \"" + materialName + "\" to the project " + curProject.getProjectName());

  }
  /**
   * collects input from user to add a category to a project
   * can link an existing category to the project or add a new one
   */
  private void addCategoryToProject() {
    if(Objects.isNull(curProject)) {
      System.out.println("\nPlease select a project");
      return;
    }
    String categoryName;    
    System.out.println("The current categories are:" + listCategories() + "but you can name new categories too!");
    categoryName = getStringInput("Enter the category you would like to add to the project");
      
    if(Objects.nonNull(categoryName)) {
      projectService.addCategoryToProject(curProject.getProjectId(), categoryName);
    } else {
      return;
    }
    System.out.println("You have entered the category \"" + categoryName + "\" to the project " + curProject.getProjectName());
    curProject = projectService.fetchProjectById(curProject.getProjectId());
  }
  /**
   * assembles category names to be printed
   * @return string consisting of category names separated by ", "
   */
  private String listCategories() {
    List<Category> categories = projectService.fetchAllCategories();
    StringBuilder list = new StringBuilder();
    for (Category category : categories) {
      list.append(category);
      list.append(", ");
    }
    return list.toString();
  }
  /**
   * removes a project from the database
   */
  private void deleteProject() {
    listProjects();
    Integer projectId = getIntInput("Enter the ID of the project you would like to delete");
    projectService.deleteProject(projectId);
    
    System.out.println("You have deleted project " + projectId);
    
    if(!Objects.isNull(curProject) && curProject.getProjectId() == projectId) {
      curProject = null;
    }

    
  }

  /**
   * collects user input to modify the fields of curProject 
   */
  private void updateProjectDetails() {
    if(Objects.isNull(curProject)) {
      System.out.println("\nPlease select a project");
      return;
    }
    String projectName = getStringInput("Enter the updated project name [" + curProject.getProjectName() + "], or press Enter to skip");
    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() +"], or press Enter to skip");
    BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "], or press Enter to skip");
    Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "], or press Enter to skip");
    String notes = getStringInput("Enter project notes [" + curProject.getNotes() + "], or press Enter to skip");
    
        
    Project updatedProject = new Project();
    updatedProject.setProjectId(curProject.getProjectId());
    updatedProject.setProjectName(Objects.isNull(projectName) 
        ? curProject.getProjectName() : projectName);
    updatedProject.setEstimatedHours(Objects.isNull(estimatedHours) 
        ? curProject.getEstimatedHours() : estimatedHours);
    updatedProject.setActualHours(Objects.isNull(actualHours) 
        ? curProject.getActualHours() : actualHours);
    updatedProject.setDifficulty(Objects.isNull(difficulty) 
        ? curProject.getDifficulty() : difficulty);
    updatedProject.setNotes(Objects.isNull(notes) 
        ? curProject.getNotes() : notes);
    
    projectService.modifyProjectDetails(updatedProject);
    
    curProject = projectService.fetchProjectById(curProject.getProjectId());
    
  }
  /**
   * lists the projects in the database and allows the user to select one of them
   */
  private void selectProject() {
    listProjects();
    Integer projectId = getIntInput("Enter project ID to select a project");
    curProject = null;
    curProject = projectService.fetchProjectById(projectId);
    
    if(Objects.isNull(curProject)) {
      System.out.println("Invalid project selected.");
    }    
  }
  /**
   * prints a list of all projects 
   */
  private void listProjects() {
    List<Project> projects = projectService.fetchAllProjects();
    System.out.println("\nProjects:");
    
    projects.forEach(project -> System.out.println("      " + project.getProjectId() + ": " + project.getProjectName()));
  }
  /**
   * collects user input to create a new project and displays the project's details
   */
  private void createProject() {
    String projectName = getStringInput("Enter the project name");
    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
    String notes = getStringInput("Enter project notes");
    
    Project project = new Project();
    project.setProjectName(projectName);
    project.setEstimatedHours(estimatedHours);
    project.setActualHours(actualHours);
    project.setDifficulty(difficulty);
    project.setNotes(notes);
    
    Project dbProject = projectService.addProject(project);
    System.out.println("You have sucessfully created project " + dbProject);
  }
  
  /**
   * shows menu, then allows user to select a project
   */
  private int getUserSelection() {
    printOptions();
    Integer input = getIntInput("Enter a menu selection");
    
    return Objects.isNull(input) ? -1 : input;
  }
  
  /**
   * translates scanner input to Integer class variable, throws exception if not possible
   */
  private Integer getIntInput(String prompt) {
    String input = getStringInput(prompt);
    if(Objects.isNull(input)) {
      return null;
    }
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      throw new DbException("That is not a number. Please try again.");
    }
  }
  
  /**
   * translates scanner input to BigDecimal class variable, throws exception if not possible
   */
  private BigDecimal getDecimalInput(String prompt) {
    String input = getStringInput(prompt);
    if(Objects.isNull(input)) {
      return null;
    }
    try {
      return new BigDecimal(input).setScale(2);
    } catch (NumberFormatException e) {
      throw new DbException("That is not a decimal number. Please try again.");
    }
  }

  /**
   * accepts String input from the scanner, with any leading or trailing spaces removed
   */
  private String getStringInput(String prompt) {
    System.out.print(prompt + ": ");
    String input = scanner.nextLine();
    
    return input.isBlank() ? null : input.trim();
  }
  
  /**
   * prints the available menu options
   */
  private void printOptions() {
    System.out.println("These are the available options. Please enter a number, or press ENTER to quit:");
    options.forEach(line -> System.out.println("  " + line));
    if(Objects.isNull(curProject)) {
       System.out.println("\nYou are not working with a project.");  
    } else {
      System.out.println("\nYou are working with project " + curProject);
    }    
  }
}
