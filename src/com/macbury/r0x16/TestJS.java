package com.macbury.r0x16;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.macbury.r0x16.rihno_sandbox.FooJsTest;
import com.macbury.r0x16.rihno_sandbox.SandboxContextFactory;

// http://codeutopia.net/blog/2009/01/02/sandboxing-rhino-in-java/
public class TestJS {

  public static void main(String[] args) {
    ContextFactory.initGlobal(new SandboxContextFactory());
    Context cx = ContextFactory.getGlobal().enterContext();
    
    try {
      Scriptable scope = cx.initStandardObjects();
      
      cx.evaluateString(scope, "var i = 3;", "ROBOT", 1, null);
      scope.put("i", scope, 2);
      cx.evaluateString(scope, "i *= 3;", "ROBOT", 1, null);
      System.err.println(scope.get("i", scope));
      Object result = cx.evaluateString(scope, "i;", "ROBOT", 2, null);
      System.err.println(Context.toString(result));
      
      Scriptable otherScope = cx.initStandardObjects();
      cx.evaluateString(otherScope, "var i = 0; function loop() { i += 1; }", "ROBOT", 1, null);
      
      for (int i = 0; i < 10; i++) {
        cx.evaluateString(otherScope, "loop();", "ROBOT", 1, null);
      }
      
      result = cx.evaluateString(otherScope, "i;", "ROBOT", 2, null);
      System.err.println(Context.toString(result));
      
      FooJsTest fjt          = new FooJsTest(); 
      Scriptable objectScope = cx.initStandardObjects();
      ScriptableObject.putProperty(objectScope, "Foo", fjt);
      cx.evaluateString(objectScope, "Foo.add();", "ROBOT", 2, null);
      cx.evaluateString(objectScope, "Foo.add();", "ROBOT", 2, null);
      System.err.println(cx.evaluateString(objectScope, "Foo.get();", "ROBOT", 2, null));
      System.err.println(fjt.get());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      Context.exit();
    }
  }
}
