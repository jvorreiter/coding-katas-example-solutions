package state_machines.ii_regular_expressions;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;

public class DynamicTransitionExample {
    private static final Character TRIGGER_CHAR_DOES_NOTHING = Character.MIN_VALUE;
    private class State {}


    private final TriggerWithParameters1<Character, State, Character> trigger = new TriggerWithParameters1<>(TRIGGER_CHAR_DOES_NOTHING, Character.class);
    private final StateMachine<State, Character> stateMachine;

    public DynamicTransitionExample() {
        stateMachine = createStateMachine();

        stateMachine.fire(trigger, 'a');
        stateMachine.fire(trigger, 'b');
        stateMachine.fire(trigger, 'c');
        // uncomment this to see an error: stateMachine.fire(trigger, 'd');
    }

    public static void main(String[] args) {
        new DynamicTransitionExample();
    }

    private StateMachine<State, Character> createStateMachine() {
        var config = new StateMachineConfig<State, Character>();

        var initialState = new State();
        var dynamicState1 = new State();
        var dynamicState2 = new State();

        config.configure(initialState)
                .permitDynamic(trigger, character -> {
                    if(character == 'a') {
                        return dynamicState1;
                    }

                    //TODO: in a Regex state machine this should probably transition to a state that represents a non-match
                    throw new IllegalStateException(String.format("Unexpected character '%s'", character));
                });


        config.configure(dynamicState1)
                .permitDynamic(trigger, character -> {
                    if(character == 'a' || character == 'b') {
                        return dynamicState2;
                    }

                    //TODO: in a Regex state machine this should probably transition to a state that represents a non-match
                    throw new IllegalStateException(String.format("Unexpected character '%s'", character));
                });

        config.configure(dynamicState2)
                .permitDynamic(trigger, character -> {
                    if(character == 'c') {
                        return dynamicState2;
                    }

                    //TODO: in a Regex state machine this should probably transition to a state that represents a non-match
                    throw new IllegalStateException(String.format("Unexpected character '%s'", character));
                });

        return new StateMachine<>(initialState, config);
    }
}
