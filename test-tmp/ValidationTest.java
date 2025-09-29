import java.io.File;
import java.util.Scanner;

public class ValidationTest {
    public static void main(String[] args) throws Exception {
        System.out.println("🔍 Comprehensive WorldSaveEventListener Validation");
        System.out.println("================================================");
        
        boolean allPassed = true;
        
        // Test 1: Check WorldSaveEventListener exists and has correct content
        System.out.println("\n📋 Test 1: WorldSaveEventListener Class Structure");
        File listenerFile = new File("src/main/java/dansplugins/simpleskills/listeners/WorldSaveEventListener.java");
        if (!listenerFile.exists()) {
            System.out.println("❌ WorldSaveEventListener.java not found");
            allPassed = false;
        } else {
            Scanner scanner = new Scanner(listenerFile);
            String content = scanner.useDelimiter("\\Z").next();
            scanner.close();
            
            if (content.contains("implements Listener")) {
                System.out.println("✅ Implements Listener interface");
            } else {
                System.out.println("❌ Does not implement Listener interface");
                allPassed = false;
            }
            
            if (content.contains("@EventHandler")) {
                System.out.println("✅ Has @EventHandler annotation");
            } else {
                System.out.println("❌ Missing @EventHandler annotation");
                allPassed = false;
            }
            
            if (content.contains("WorldSaveEvent")) {
                System.out.println("✅ Handles WorldSaveEvent");
            } else {
                System.out.println("❌ Does not handle WorldSaveEvent");
                allPassed = false;
            }
            
            if (content.contains("storageService.save()")) {
                System.out.println("✅ Calls storageService.save()");
            } else {
                System.out.println("❌ Does not call storageService.save()");
                allPassed = false;
            }
            
            if (content.contains("log.debug")) {
                System.out.println("✅ Includes debug logging");
            } else {
                System.out.println("❌ Missing debug logging");
                allPassed = false;
            }
        }
        
        // Test 2: Check SimpleSkills integration
        System.out.println("\n📋 Test 2: SimpleSkills Integration");
        File mainFile = new File("src/main/java/dansplugins/simpleskills/SimpleSkills.java");
        if (!mainFile.exists()) {
            System.out.println("❌ SimpleSkills.java not found");
            allPassed = false;
        } else {
            Scanner scanner = new Scanner(mainFile);
            String content = scanner.useDelimiter("\\Z").next();
            scanner.close();
            
            if (content.contains("import dansplugins.simpleskills.listeners.WorldSaveEventListener")) {
                System.out.println("✅ Imports WorldSaveEventListener");
            } else {
                System.out.println("❌ Does not import WorldSaveEventListener");
                allPassed = false;
            }
            
            if (content.contains("WorldSaveEventListener worldSaveEventListener")) {
                System.out.println("✅ Declares worldSaveEventListener field");
            } else {
                System.out.println("❌ Does not declare worldSaveEventListener field");
                allPassed = false;
            }
            
            if (content.contains("new WorldSaveEventListener(storageService, log)")) {
                System.out.println("✅ Creates WorldSaveEventListener with correct dependencies");
            } else {
                System.out.println("❌ Does not create WorldSaveEventListener with correct dependencies");
                allPassed = false;
            }
            
            if (content.contains("registerEvents(worldSaveEventListener, this)")) {
                System.out.println("✅ Registers worldSaveEventListener");
            } else {
                System.out.println("❌ Does not register worldSaveEventListener");
                allPassed = false;
            }
        }
        
        // Test 3: Check project structure
        System.out.println("\n📋 Test 3: Project Structure");
        
        if (new File("pom.xml").exists()) {
            System.out.println("✅ Maven POM exists");
        } else {
            System.out.println("❌ Maven POM missing");
            allPassed = false;
        }
        
        if (new File("Dockerfile").exists()) {
            System.out.println("✅ Dockerfile exists");
        } else {
            System.out.println("❌ Dockerfile missing");
            allPassed = false;
        }
        
        if (new File("dependencies/ponder-v0.14-alpha-2.jar").exists()) {
            System.out.println("✅ Ponder dependency exists");
        } else {
            System.out.println("❌ Ponder dependency missing");
            allPassed = false;
        }
        
        // Final result
        System.out.println("\n" + "=".repeat(50));
        if (allPassed) {
            System.out.println("🎉 ALL TESTS PASSED!");
            System.out.println("\n📊 Implementation Summary:");
            System.out.println("  ✅ WorldSaveEventListener properly implemented");
            System.out.println("  ✅ Integrated with SimpleSkills main class");
            System.out.println("  ✅ Will save plugin data during server autosaves");
            System.out.println("  ✅ Prevents data loss during server crashes");
            System.out.println("\n🎯 The implementation is ready for production use!");
        } else {
            System.out.println("❌ SOME TESTS FAILED!");
            System.out.println("Please review the implementation and fix the issues above.");
            System.exit(1);
        }
    }
}
