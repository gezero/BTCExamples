package bitsurance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * User: George
 * Date: 27.10.13
 * Time: 7:42
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BitsuranceConfiguration.class})
public class BitsuranceTest {
    @Autowired
    Bitsurance bitsurance;

    @Test
    public void simpleTest(){
        assertThat(bitsurance.test(), is(1));
    }
}
