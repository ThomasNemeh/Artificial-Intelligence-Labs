'''Critter that learns optimal behavior based on probabilities that shift with negative and positive reinforcement. 
   Implemented with a list that maintains memory of all interactions'''
import critter
import color 
import random

class Tnemeh2(critter.Critter):
    # Memory initialized with one instance of each strategy
    strategyMem = [critter.HEAL, critter.PARTY, critter.ROAR, critter.POUNCE, critter.SCRATCH]
    directions=[critter.NORTH, critter.SOUTH, critter.WEST, critter.EAST]

    def __init__(self):
        super().__init__()
        self.strategy = None
     
    # @param oppInfo The critter info of the current opponent.
    # @returns Your action: ROAR, POUNCE, SCRATCH, PARTY, or HEAL
    def interact(self, oppInfo):
        #pick strategy based on what memory indicates is most likely to be sucessful
        self.strategy = random.choice(Tnemeh2.strategyMem)
        while self.strategy is critter.HEAL and oppInfo.getNeighborHealth(critter.CENTER) is 100:
            self.strategy = random.choice(self.strategyMem)

        return self.strategy
    
    # Give your color.
    # @returns Your current color.
    def getColor(self):
        return color.ORANGE
    
    # Give your direction.
    # @param info your critter info
    # @returns A cardinal direction, in the form of a constant (NORTH, SOUTH)
    def getMove(self, info):
        for move in Tnemeh2.directions:
            if info.getNeighbor(move) is not ".":
                return move
                
        return random.choice(Tnemeh2.directions)
    
    # Give your character.
    # @returns Whichever character represents this critter.
    def getChar(self):
        return '2'
    
    # End of interaction shenanigans.
    # @param won Boolean; true if won the interaction, false otherwise.
    # @param oppFight Opponent's choide of fight strategy (ROAR, etc)
    # @returns Nothing.
    def interactionOver(self, won, oppFight):
        '''add successful strategy to memory and remove unsucessful strategy from memory, ensuring that there is at least one
           instance of each strategy in memory'''
        if oppFight is critter.ROAR or oppFight is critter.POUNCE or oppFight is critter.SCRATCH:
            if won is True:
                Tnemeh2.strategyMem.append(self.strategy)
            else:
                if Tnemeh2.strategyMem.count(self.strategy) > 1:
                    Tnemeh2.strategyMem.remove(self.strategy)
        else:
            if self.strategy is critter.ROAR or self.strategy is critter.SCRATCH or self.strategy is critter.POUNCE:
                if Tnemeh2.strategyMem.count(self.strategy) > 1:
                    Tnemeh2.strategyMem.remove(self.strategy)
            elif self.strategy is critter.PARTY:
                Tnemeh2.strategyMem.append(self.strategy)
            else:
                Tnemeh2.strategyMem.append(self.strategy)
                Tnemeh2.strategyMem.append(self.strategy)


            
    

        
            
                 
            


