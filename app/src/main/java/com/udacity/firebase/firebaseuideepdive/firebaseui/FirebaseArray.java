/*
 * Firebase UI Bindings Android Library
 *
 * Copyright © 2015 Firebase - All Rights Reserved
 * https://www.firebase.com
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binaryform must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY FIREBASE AS IS AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL FIREBASE BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.udacity.firebase.firebaseuideepdive.firebaseui;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

/**
 * This class implements an array-like collection on top of a Firebase location.
 */
class FirebaseArray implements ChildEventListener {
    public interface OnChangedListener {
        enum EventType { Added, Changed, Removed, Moved }
        void onChanged(EventType type, int index, int oldIndex);
    }

    private Query mQuery;
    private OnChangedListener mListener;
    /**
     * This is the local array (well ArrayList) representing the list of objects in the
     * Firebase database. It is made up of DatabaseSnapshots of each specific item.
     */
    private ArrayList<DataSnapshot> mSnapshots;

    public FirebaseArray(Query ref) {
        mQuery = ref;
        mSnapshots = new ArrayList<DataSnapshot>();
        mQuery.addChildEventListener(this);
    }

    public void cleanup() {
        mQuery.removeEventListener(this);
    }

    public int getCount() {
        return mSnapshots.size();

    }
    public DataSnapshot getItem(int index) {
        return mSnapshots.get(index);
    }

    /**
     *  This method searches through the mSnapshots array for the object with the given key
     *  and returns the index of the object in the array.
     */
    private int getIndexForKey(String key) {
        int index = 0;
        for (DataSnapshot snapshot : mSnapshots) {
            if (snapshot.getKey().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    // Start of ChildEventListener methods

    /**
     * ChildEventListener is an interface that allows for a class to listen to when
     * the children of a list of data in Firebase has changed. Here are the four methods
     * That must be overriden. These methods control what happens with a child is added, removed,
     * changed or moved within the list.
     * In this case, these methods update the local array called mSnapshots to match whatever change
     * has happened in the data base. mSnapshots is made up of database snapshots of each of the
     * items in the list.
     */

    /**
     * For example, onChildAdded is triggered when a child is added to the list
     * in Firebase. This method here is finding the position of the child added in Firebase
     * and adding the child in the ArrayList at the same location.
     */
    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
        int index = 0;
        /**
         * If the previous child is null, then this child is being inserted at the
         * start of the list and should have index 0.
         */
        if (previousChildKey != null) {
            /** Find the index of the previous child and add 1 to make the index
             * for this newly added child.
             */
            index = getIndexForKey(previousChildKey) + 1;
        }
        /* Update the local array */
        mSnapshots.add(index, snapshot);
        /* Notify the FirebaseListAdapter that the underlying array has changed */
        notifyChangedListeners(OnChangedListener.EventType.Added, index);
    }

    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
        // TODO Update the mSnapshots array so that the snapshot in the local array
        // is updated. snapshot is the updated DataSnapshot. It is at the same location
        // but some of its data has changed from whatever is currently stored in your
        // local array.

        // TODO Don't forget to notify the FirebaseListAdapter that the underlying
        // array has changed. You should use the OnChangedListener.EventType.Changed
        // EventType.
    }

    public void onChildRemoved(DataSnapshot snapshot) {
        // TODO Update the mSnapshots array so that the snapshot which is passed
        // in is removed from the local array.

        // TODO Don't forget to notify the FirebaseListAdapter that the underlying
        // array has changed. You should use the OnChangedListener.EventType.Removed
        // EventType.
    }

    public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
        /* Get the current location of the child being moved */
        int oldIndex = getIndexForKey(snapshot.getKey());
        /* Remove it from its current location */
        mSnapshots.remove(oldIndex);
        /* Find the new index of the child in the same way we did for onChildAdded */
        int newIndex = previousChildKey == null ? 0 : (getIndexForKey(previousChildKey) + 1);
        /* Add the child at that index */
        mSnapshots.add(newIndex, snapshot);
        /* Notify the FirebaseListAdapter that the underlying array has changed */
        notifyChangedListeners(OnChangedListener.EventType.Moved, newIndex, oldIndex);
    }

    public void onCancelled(FirebaseError firebaseError) {
    }
    // End of ChildEventListener methods

    public void setOnChangedListener(OnChangedListener listener) {
        mListener = listener;
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index) {
        notifyChangedListeners(type, index, -1);
    }
    protected void notifyChangedListeners(OnChangedListener.EventType type, int index, int oldIndex) {
        if (mListener != null) {
            mListener.onChanged(type, index, oldIndex);
        }
    }
}
