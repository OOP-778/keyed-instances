# Keyed Instances
![Maven Central](https://img.shields.io/maven-central/v/dev.oop778/keyed-instances)
![Static Badge](https://img.shields.io/badge/Java_version-8-brightgreene)
![Scc Count Badge](https://sloc.xyz/github/oop-778/keyed-instances?category=code)

## What's Keyed Instances?

This library allows you to register your instances based on unique key & groups as defined by `KeyedGroupId`.

[## Features
* Register objects implementing KeyedInstance interface
* Retrieve objects by a full path for example `currency.tokens` or if you know the parent class you can just retrieve them by defining parent and just `tokens`
* Retrieve keyed references, this is useful for configurations that can be reloaded at runtime, so you can keep reference and just get the up-to-date object.
* Create "unresolved" references. This is also useful for whenever your configs depend on other objects that can be identified and resolved lazily.
* Find all implementations of a specific group
* Each KeyedInstance can have multiple root interfaces aka you can have `group_a` and `group_b` and retrieve the instance by either of one these `group_x.<instnace_key>`

### Example
```java
    public static void main(String[] args) {
        // Create the registry
        final KeyedRegistry registry = KeyedRegistry.builder().build();

        final GroupABImpl instance = new GroupABImpl();

        // Register object
        registry.registerInstance(instance);

        // Retrieve by class
        registry.<GroupABImpl>find().single().withInstance(GroupABImpl.class).firstOrNull();

        // Retrieve by path
        registry.<GroupABImpl>find().single().withInstance("group_a.inner.impl").firstOrNull();

        // Get full path of an instance (if instance has multi roots, it'll use first one)
        registry.getInstancePath(instance);

        // Get reference of instance
        registry.<GroupABImpl>find().singleAsReference().withInstance(GroupABImpl.class).firstOrNull();

        // Get unresolved reference
        registry.<GroupABImpl>find().singleAsReference().withInstance("group_a.inner.not_existing_yet").firstOrCreateUnresolvedReference();

        // Get path from a parent (this will return just inner.impl)
        registry.getInstancePathFrom(GroupA.class, instance);
    }

    @KeyedGroupId("group_a")
    public static interface GroupA extends KeyedInstance {}

    @KeyedGroupId("group_b")
    public static interface GroupB extends KeyedInstance {}

    @KeyedGroupId("inner")
    public static interface InnerGroup extends GroupA, GroupB {}

    public static class GroupABImpl implements InnerGroup {
        @Override
        public @NonNull String getKey() {
            return "impl";
        }
    }
```

### Structure
![Diagram](img/structure.png)