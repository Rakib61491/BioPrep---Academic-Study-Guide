package com.example.bioprep.adapters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class FruitRecyclerAdapterTest {

    @Test
    public void testFruitRecyclerAdapterItemCount() {
        String[] names = {"Apple", "Mango", "Banana"};
        String[] descriptions = {"Pome fruit", "Drupe fruit", "Berry fruit"};

        FruitRecyclerAdapter adapter = new FruitRecyclerAdapter(names, descriptions);
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testFruitRecyclerAdapterEmptyArray() {
        FruitRecyclerAdapter adapter = new FruitRecyclerAdapter(new String[0], new String[0]);
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFruitRecyclerAdapterNullSafety() {
        FruitRecyclerAdapter adapter = new FruitRecyclerAdapter(null, null);
        assertEquals(0, adapter.getItemCount());
    }
}

