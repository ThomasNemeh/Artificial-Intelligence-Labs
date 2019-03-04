'''critter than chooses direction and interaction strategy randomly, with equal probability'''
import critter
import color 
import random

class Tnemeh5(critter.Critter):

    directions=[critter.NORTH, critter.SOUTH, critter.WEST, critter.EAST]
    strategies = [critter.HEAL, critter.PARTY, critter.ROAR, critter.POUNCE, critter.SCRATCH]

    def __init__(self):
        super().__init__()
        
    # @param oppInfo The critter info of the current opponent.
    # @returns Your action: ROAR, POUNCE, SCRATCH, PARTY, or HEAL
    def interact(self, oppInfo):
        return random.choice(Tnemeh5.strategies)
    
    # Give your color.
    # @returns Your current color.
    def getColor(self):
        return color.GRAY
    
    # Give your direction.
    # @param info your critter info
    # @returns A cardinal direction, in the form of a constant (NORTH, SOUTH)
    def getMove(self, info):
        return random.choice(Tnemeh5.directions)
    
    # Give your character.
    # @returns Whichever character represents this critter.
    def getChar(self):
        return '5'
    
    # End of interaction shenanigans.
    # @param won Boolean; true if won the interaction, false otherwise.
    # @param oppFight Opponent's choide of fight strategy (ROAR, etc)
    # @returns Nothing.
    def interactionOver(self, won, oppFight):
        pass

