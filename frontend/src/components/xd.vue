<template>
  <div ref="d3Container"></div>
</template>

<script>
import * as d3 from 'd3'

export default {
  name: 'D3Tree',
  data() {
    return {
      treeData: [
        {
          name: 'Top Level',
          parent: 'null',
          children: [
            {
              name: 'Level 2: A',
              parent: 'Top Level',
              children: [
                {
                  name: 'Son of A',
                  parent: 'Level 2: A'
                },
                {
                  name: 'Daughter of A',
                  parent: 'Level 2: A'
                },
                {
                  name: 'Another daughter of A',
                  parent: 'Level 2: A'
                }
              ]
            },
            {
              name: 'Level 2: B',
              parent: 'Top Level',
              children: [
                {
                  name: 'Son of B',
                  parent: 'Level 2: B'
                },
                {
                  name: 'Daughter of B',
                  parent: 'Level 2: B'
                },
                {
                  name: 'Another daughter of B',
                  parent: 'Level 2: B'
                }
              ]
            }
          ]
        }
      ]
    }
  },
  mounted() {
    this.createTree()
  },
  methods: {
    createTree() {
      const margin = { top: 20, right: 120, bottom: 20, left: 120 }
      const width = 960 - margin.right - margin.left
      const height = 500 - margin.top - margin.bottom

      const i = 0
      const duration = 750

      this.tree = d3.layout.tree().size([height, width])

      const diagonal = d3.svg.diagonal().projection((d) => {
        return [d.y, d.x]
      })

      var svg = d3
        .select(this.$refs.d3Container)
        .append('svg')
        .attr('width', width + margin.right + margin.left)
        .attr('height', height + margin.top + margin.bottom)
        .append('g')
        .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')

      const root = this.treeData[0]
      root.x0 = height / 2
      root.y0 = 0

      this.update(root)

      // d3.select(self.frameElement).style("height", "500px"); // This line may not be necessary in Vue
    },
    update(source) {
      const nodes = this.tree.nodes(source).reverse()
      const links = this.tree.links(nodes)

      nodes.forEach(function (d) {
        d.y = d.depth * 180
      })

      // ... rest of your update function ...
    }
  }
}
</script>

<style>
.node {
  cursor: pointer;
}
/* ... rest of your CSS ... */
</style>
