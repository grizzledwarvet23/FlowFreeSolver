# FlowFreeSolver

![image](https://user-images.githubusercontent.com/72232563/207208299-7717234a-e8ac-49a7-b596-ae33faa55613.png)


A Solver to the game Flow, a mobile color matching puzzle game. I had thought the game could easily be mapped to computer science since it is played within a grid and is related to pathfinding.

It's recursive backtracking approach, so it's not the most efficient. But, it has a few heuristics:
- It sorts pairs of colored endpoints from closest to farthest, so it starts by solving for paths that are likely to be shorter and more direct (and thus, quicker to find the "correct" solution)
- In deciding which direction to move next for a path, it chooses the direction in order of which is closer to the endpoint. For instance, in this grid:
![image](https://user-images.githubusercontent.com/72232563/207211814-3e1362f0-6d81-47e8-a2c3-8552976feb55.png)
A move starting from the orange in the 0-indexed row & col (1, 5) will start with a move to the left, while a move starting from the yellow at (4, 4) starts with a move to the right (which ends up being the correct solution for yellow). That is because we are sorting by lowest euclidian distance.

Anyways, some improvements can be made and there have been some discussions on solving flow already.
