package io.github.drakonkinst.worldsinger.entity;

public interface CameraPossessable {

    int FIRST_PERSON = 0;
    int THIRD_PERSON_BACK = 1;
    int THIRD_PERSON_FRONT = 2;

    default int getDefaultPerspective() {
        return FIRST_PERSON;
    }

    default boolean canUseItems() {
        return false;
    }

    default boolean canSwitchPerspectives() {
        return false;
    }
}
