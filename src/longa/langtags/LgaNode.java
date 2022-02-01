package longa.langtags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Target(ElementType.METHOD|ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LgaNode
{
    public int min=1;

    public int max=1;

    /** 参数前置个数 */
    public int prepCount() default 0;

    /** 必填一次 */
    public LgaMethodReqKind once() default LgaMethodReqKind.any;

}
