/*
 * TestModule.java
 *
 * Created on 24 Август 2008 г., 16:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package modules;

/**
 *
 * @author evgs
 */
public class TestModule implements BombusModule{
    
    public final static String MODID="testmodule";
    /** Creates a new instance of TestModule */
    public TestModule() {
    }

    public String getModuleName() { return MODID; }

    public void helloWorld() {
        System.out.println("Hello modular world!");
    }
    
}
