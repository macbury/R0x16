package com.macbury.r0x16.rihno_sandbox;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class SandboxContextFactory extends ContextFactory {

  @Override
  protected Context makeContext() {
    System.err.println("Creating context!");
    Context cx = super.makeContext();
    cx.setWrapFactory(new SandboxWrapFactory());
    cx.setClassShutter(new ClassShutter() {
      @Override
      public boolean visibleToScripts(String fullClassName) {
        if(fullClassName.contains("FooJsTest"))
          return true;
     
        return false;
      }
    });
    return cx;
  }
  
}
