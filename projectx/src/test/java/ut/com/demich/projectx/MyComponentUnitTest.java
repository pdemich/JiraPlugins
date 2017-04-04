package ut.com.demich.projectx;

import org.junit.Test;
import com.demich.projectx.api.MyPluginComponent;
import com.demich.projectx.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}