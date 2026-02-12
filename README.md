# Soft Body Simulation - Spring-Mass Model

A very simuple 2D soft body physics simulation implemented in Java using spring-mass systems and numerical ODE solvers. This project was developed as a final project for the Computational Physics course (Spring 2025) I took at Lehigh University.



https://github.com/user-attachments/assets/806290d1-861f-494b-bfc6-a9f71d992195



## Model Description

The soft body is constructed as a circular arrangement of N nodes connected by springs:
- **Surface springs** connect adjacent nodes on the perimeter (blue springs in diagrams)
- **Radial springs** connect each surface node to the center (green springs in diagrams)
- **Area-preserving forces** simulate internal pressure by maintaining the polygon's area close to equilibrium

Each node is treated as a point mass subject to:
- Spring forces (Hooke's law with damping)
- Area-restoring forces (pressure-like)
- Gravity and air drag

The system is evolved using the **Verlet integration** method for numerical stability.

### Key Features
- Custom `vec2` class for 2D vector operations
- Abstract ODE solver framework (`AbstractODESolver2D`, `Verlet2D`)
- Configurable spring constants, damping coefficients, and air resistance
- Real-time visualization using Open Source Physics (OSP) library
- Analysis tools for phase space plots and area vs. time tracking

**For more details:** Check my [presentation slides](projectPresentation/main.pptx) from the final exam.


## Files overview

I have not tried to organize the project files. Here's the main ones though:

### Soft body app java files

- [Soft body class](springMass/src/SoftBall/softBall2.java)
- [Soft body app](springMass/src/SoftBall/softBall2App.java)

## Analysis & Visualization

The `analysisScripts` have contain some scripts for physical quantification of the model:

- **Phase space plots** showing position vs. velocity for individual nodes
- **Area vs. time plots** demonstrating area conservation or dissipation
- **Energy tracking** for different damping regimes


## Goals Achieved 

- Implementation of spring-mass soft body
- Soft body using finite element analysis and comparison of compute performance with spring mass for different systems

## Future Work (TODO)

The following features are planned for future development:

- [ ] **3D Soft Bodies** - Extend the model to three dimensions using tetrahedral meshes
- [ ] **Collision Detection** - Implement collision of soft bodies with rigid obstacles
- [ ] **Interactive Analysis Interface** - Create an interactive drawing frame for real-time parameter tuning and visualization
- [ ] **Parallelization** - Optimize compute performance using multi-threading for large-scale simulations

## Technical Highlights

### Custom Vector Class
Implemented a full-featured 2D vector class (`vec2.java`) to:
- Avoid managing individual x/y components separately
- Provide clean, readable physics code
- Support operations: addition, subtraction, dot product, scalar multiplication, normalization

### OOP Architecture
- Abstract classes and interfaces for ODE solvers
- Drawable interface implementation for OSP visualization
- Modular design for easy extension to new soft body geometries

### Numerical Methods
- Verlet integration for time evolution (symplectic, energy-conserving)
- Configurable time steps and solver parameters

## References

- Open Source Physics library: [https://www.compadre.org/osp/](https://www.compadre.org/osp/)




