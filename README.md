# FlowFreeSolver

![image](https://user-images.githubusercontent.com/72232563/207208299-7717234a-e8ac-49a7-b596-ae33faa55613.png)


A Solver to the game Flow, a mobile color matching puzzle game. I had thought the game could easily be mapped to computer science since it is played within a grid and is related to pathfinding.

It's recursive backtracking approach, so it's not the most efficient. But, it has a few heuristics:
- It sorts pairs of colored endpoints from closest to farthest, so it starts by solving for paths that are likely to be shorter and more direct (and thus, quicker to find the "correct" solution)
- In deciding which direction to move next for a path, it chooses the direction in order of which is closer to the endpoint. For instance, in this grid:

![image](https://user-images.githubusercontent.com/72232563/207211814-3e1362f0-6d81-47e8-a2c3-8552976feb55.png)

A move starting from the orange in the 0-indexed row & col (1, 5) will start with a move to the left, while a move starting from the yellow at (4, 4) starts with a move to the right (which ends up being the correct solution for yellow). That is because we are sorting by lowest euclidian distance.

Anyways, some improvements can be made, and there have been some discussions on solving flow already.

Some observations: 
- The solver works relatively quickly up to grids of size 7x7. This could be a factor of new colors rather than size, however.
- Grids where the paths of colors are very counterintuitive/not direct are more difficult to solve. Note this grid:

<img width="341" alt="image" src="https://user-images.githubusercontent.com/72232563/207385760-cd71110c-8ba2-42c7-9234-30e25bf10447.png">

And the solution (produced in my visualizer): 

<img width="134" alt="image" src="https://user-images.githubusercontent.com/72232563/207385846-b058456c-4ebe-4564-8a65-3fdfed723da4.png">

Especially that orange path, the endpoints are really close together but the path ends up being long. This took 2.9537 seconds to solve on my machine. Meanwhile, most of the average looking solutions like this:

<img width="120" alt="image" src="https://user-images.githubusercontent.com/72232563/207386328-af63c0d1-06b8-41f3-b21e-4f57d1b7413f.png">

take less than 0.1 seconds.

Data (in seconds): 
6x6 Mania times - 

l1: 4.968E-4

l2: 0.154402

l3: 0.0017776

l4: 0.1043191

l5: 0.0211899

7x7 mania times - 

l83: 3.1592133

l84: 0.0139016

l85: 3.2735783

l86: 0.4208853

8x8 mania times - 
l25: 


