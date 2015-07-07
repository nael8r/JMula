package jmula;

import jmula.simul.clSimul;

/**
 * Created by nael on 29/06/15.
 */
public class JMula
{
	public static void main(String[] args)
	{
		clSimul s = new clSimul();

		s.simul(2, 5760000, System.currentTimeMillis());


		System.out.println();
	}
}
