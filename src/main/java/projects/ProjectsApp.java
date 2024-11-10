package projects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
  //@formatter:off
  private List<String> options = List.of(
      "1) Add a project",
      "2) List projects",
      "3) Select a project"
      
      );
  // @formatter:on
  private Scanner scanner = new Scanner(System.in);
  ProjectService projectService = new ProjectService();
  Project curProject;
  
  public static void main(String[] args) {
    new ProjectsApp().processUserSelections();
  }
  /*
   * THE MENU
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
          
          default:
            System.out.println("\n" + selection + " is not a valid selection.");
           
        }
      } catch (Exception e) {
        System.out.println("\nError: " + e + " Please try again.");
      }
    } 
  }

  private void selectProject() {
    listProjects();
    Integer projectId = getIntInput("Enter project ID to select a project");
    curProject = null;
    curProject = projectService.fetchProjectById(projectId);
    
    if(Objects.isNull(curProject)) {
      System.out.println("Invalid project selected.");
    }
    
    
    
    
  }
  
  private void listProjects() {
    List<Project> projects = projectService.fetchAllProjects();
    System.out.println("\nProjects:");
    
    projects.forEach(project -> System.out.println("      " + project.getProjectId() + ": " + project.getProjectName()));
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
  
  /**
   * shows menu, then handles user choice
   */
  private int getUserSelection() {
    printOptions();
    Integer input = getIntInput("Enter a menu selection");
    
    return Objects.isNull(input) ? -1 : input;
  }
  
  /**
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
  /**
   * translates scanner input to BigDecimal, or throws exception if not possible
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
   * accepts input from the scanner
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
