package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
  //@formatter:off
  private List<String> options = List.of(
      "1) Add a project"
      );
  // @formatter:on
  private Scanner scanner = new Scanner(System.in);
  ProjectService projectService = new ProjectService();
  
  public static void main(String[] args) {
    new ProjectsApp().processUserSelections();
  }
  /*
   * 
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
          
          default:
            System.out.println("\n" + selection + " is not a valid selection. Please try again.");
            
          
        }
      } catch (Exception e) {
        System.out.println("\nError: " + e + " Try again.");
      }
    } 
  }

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
  
  /*
   * shows menu, then handles user choice
   */
  private int getUserSelection() {
    printOptions();
    Integer input = getIntInput("Enter a menu selection");
    
    return Objects.isNull(input) ? -1 : input;
  }
  
  /*
   * translates scanner input to Integer, or throws exception if not possible
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

  /*
   * accepts input from the scanner
   */
  private String getStringInput(String prompt) {
    System.out.print(prompt + ": ");
    String input = scanner.nextLine();
    
    return input.isBlank() ? null : input.trim();
  }
  
  /*
   * prints each of the available menu options
   */
  private void printOptions() {
    System.out.println("These are the available options. Please enter a number, or press ENTER to quit:");
    options.forEach(line -> System.out.println("  " + line));
    
  }
}
