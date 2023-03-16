package verifier.util;

import java.io.*;
import java.util.*;

public class Pair<A, B>
        implements
        Serializable
{
    private static final long serialVersionUID = 8176288675989092842L;

    /**
     * The first pair element
     */
    protected A first;

    /**
     * The second pair element
     */
    protected B second;

    /**
     * Create a new pair
     *
     * @param a the first element
     * @param b the second element
     */
    public Pair(A a, B b)
    {
        this.first = a;
        this.second = b;
    }

    /**
     * Get the first element of the pair
     *
     * @return the first element of the pair
     */
    public A getFirst()
    {
        return first;
    }

    /**
     * Get the second element of the pair
     *
     * @return the second element of the pair
     */
    public B getSecond()
    {
        return second;
    }

    /**
     * Set the first element of the pair.
     *
     * @param f the element to be assigned.
     */

    public void setFirst(A f)
    {
        first = f;
    }

    /**
     * Set the second element of the pair.
     *
     * @param s the element to be assigned.
     */

    public void setSecond(B s)
    {
        second = s;
    }

    /**
     * Assess if this pair contains an element.
     *
     * @param e The element in question
     *
     * @return true if contains the element, false otherwise
     *
     * @param <E> the element type
     */
    @SuppressWarnings("unlikely-arg-type")
    public <E> boolean hasElement(E e)
    {
        if (e == null) {
            return first == null || second == null;
        } else {
            return e.equals(first) || e.equals(second);
        }
    }

    @Override
    public String toString()
    {
        return "(" + first + "," + second + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        else if (!(o instanceof Pair))
            return false;

        @SuppressWarnings("unchecked") Pair<A, B> other = (Pair<A, B>) o;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(first, second);
    }

    /**
     * Create a new pair.
     *
     * @param a first element
     * @param b second element
     * @param <A> the first element type
     * @param <B> the second element type
     * @return new pair
     */
    public static <A, B> Pair<A, B> of(A a, B b)
    {
        return new Pair<>(a, b);
    }

}
