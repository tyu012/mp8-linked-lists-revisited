# Mini-Project 8: Linked lists, revisited

https://github.com/tyu012/mp8-linked-lists-revisited

## Author

Tim Yu

## Purpose

Revisiting our implementation of doubly-linked lists, considering the effects of
a well-known variant.

Showing that a circular doubly-linked list with a dummy node reduces the number
of edge cases we need to consider in the iterator code.

Implementing a "fail-fast" strategy for list iterators.

## Acknowledgements

This mini-project contains files from the doubly-linked lists and circularly-linked
lists lab:
* https://rebelsky.cs.grinnell.edu/Courses/CSC207/2024Sp/labs/doubly-linked-lists

The lab was completed as a group with Keely Miyamoto and Nye Tenerelli.

As always, we have the Java® Platform, Standard Edition & Java Development Kit
Version 17 API Specification:
* https://docs.oracle.com/en/java/javase/17/docs/api/index.html

## Instructions for running

Use the `SimpleCDLL` class in your code to incorporate the circular doubly-linked
lists.

Run `SCDLLExpt.java` to see a demonstration of `SimpleCDLL`.

## Benefits of using a dummy node and a circularly linked list

A dummy node represents both the beginning and end of the linked list.
Therefore, having a dummy node allows the `prev` and `next` pointers of the
iterator to point to a node rather than `null` when the iterator is located
"before" the first element or "after" the last element.
This benefits us by preventing the need for having an edge case we have to consider
when we are inserting an element to the front of the list.
Instead, if the iterator is located in the front of the list, we can just
insert the new element after the dummy using the `insertAfter` method in `Node2`.
At the same time, we do not need to update the front of the list anymore.
Instead, we can computationally keep track of the front of the list using a method
through letting this method return the next node pointed by the dummy.

A circularly linked list is useful for eliminating edge cases when inserting
a node to an empty list.
When we call `insertAfter` on the dummy node of an empty list, the new node
points forwards and backwards to the dummy node.
This is because in an empty list, the dummy node points forwards and backwards
to itself, so when calling `insertAfter`, the `prev` field of the "next" node of the dummy node,
which is itself, points to the new node, while the `next` field of this dummy node
also points to the new node.
If we begin with the iterator lying between the front and back of an empty list,
it will point two times to the dummy node.
So, after adding an element, it will accordingly lie after the first element,
pointing back to the first element and forwards to the dummy node without handling
an edge case.