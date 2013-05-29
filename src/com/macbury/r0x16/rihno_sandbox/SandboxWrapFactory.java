package com.macbury.r0x16.rihno_sandbox;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class SandboxWrapFactory extends WrapFactory {
  @Override
  public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType) {
    return new SandboxNativeJavaObject(scope, javaObject, staticType);
  }
}
