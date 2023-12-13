<template>
  <div class="mx-auto flex flex-col">
    <div class="mx-auto" id="d3container" />
    <div class="flex flex-row border">
      <div class="mx-auto flex flex-row">
        <form
          method="post"
          class="login-form z-2 mx-auto flex w-80 flex-col p-6"
          @submit.prevent="addChild"
        >
          <label for="parentIdField">Podaj id rodzica</label>
          <InputField
            v-model="parentIdField"
            id="parentIdField"
            class="mx-auto mb-2 flex h-8 w-full rounded-md border border-black"
          />
          <label for="childNameField">Podaj imię dziecka</label>
          <InputField
            v-model="childNameField"
            id="childNameField"
            class="mx-auto mb-2 flex h-8 w-full rounded-md border border-black"
          />
          <button type="submit" class="hover:bg-krygo-burgundy h-8 rounded-md bg-black text-white">
            Dodaj dziecko
          </button>
        </form>
        <form
          method="post"
          class="login-form z-2 mx-auto flex w-80 flex-col p-6"
          @submit.prevent="deleteNode"
        >
          <label for="nodeIdField">Podaj id węzła</label>
          <InputField
            v-model="nodeIdField"
            id="nodeIdField"
            class="mx-auto mb-2 flex h-8 w-full rounded-md border border-black"
          />
          <button class="hover:bg-krygo-scarlet h-8 rounded-md bg-black text-white">
            Usuń węzeł
          </button>
        </form>
        <form
          method="post"
          class="login-form z-2 mx-auto flex w-80 flex-col p-6"
          @submit.prevent="deleteEverything"
        >
          <label for="nodeIdField">Usuń wszystkie węzły</label>
          <button
            class="bg-krygo-n-red text-krygo-hre-gold h-8 rounded-md font-medium hover:text-black"
          >
            Usuń wszystko
          </button>
        </form>
        <form
          method="post"
          class="login-form z-2 mx-auto flex w-72 flex-col p-6"
          @submit.prevent="calculateSiblings"
        >
          <label for="calculateSiblingsIdField">Podaj id węzła</label>
          <InputField
            v-model="calculateSiblingsIdField"
            id="calculateSiblingsIdField"
            class="mx-auto mb-2 flex h-8 w-full rounded-md border border-black"
          />
          <button
            class="hover:bg-krygo-hre-gold h-8 rounded-md bg-black text-white hover:text-black"
          >
            Policz rodzeństwo
          </button>
          <div v-if="siblingsCount != ''">Węzeł ma {{ siblingsCount }} rodzeństwa</div>
        </form>
        <form
          method="post"
          class="login-form z-2 mx-auto flex w-72 flex-col p-6"
          @submit.prevent="calculateCousins"
        >
          <label for="calculateCousinsIdField">Podaj id węzła</label>
          <InputField
            v-model="calculateCousinsIdField"
            id="calculateCousinsIdField"
            class="mx-auto mb-2 flex h-8 w-full rounded-md border border-black"
          />
          <button
            class="hover:bg-krygo-hre-gold h-8 rounded-md bg-black text-white hover:text-black"
          >
            Policz kuzynów
          </button>
          <div v-if="cousinsCount != ''">Węzeł ma {{ cousinsCount }} kuzynów</div>
        </form>
        <form
          method="post"
          class="login-form z-2 mx-auto flex w-72 flex-col p-6"
          @submit.prevent="addNode"
        >
          <label for="addNodeField">Podaj imię</label>
          <InputField
            v-model="addNodeField"
            id="addNodeField"
            class="mx-auto mb-2 flex h-8 w-full rounded-md border border-black"
          />
          <button
            class="hover:bg-krygo-hre-gold h-8 rounded-md bg-black text-white hover:text-black"
          >
          Dodaj pierwszy węzeł
        </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import * as d3 from 'd3'
import axios from 'axios'
import InputField from '@/components/InputField.vue'

export default {
  name: 'SimpleGraph',
  components: {
    InputField
  },
  data() {
    return {
      fetchedTreeData: '',
      nodes: [],
      links: [],
      parentIdField: '',
      childNameField: '',
      nodeIdField: '',
      calculateSiblingsIdField: '',
      siblingsCount: '',
      calculateCousinsIdField: '',
      cousinsCount: '',
      addNodeField: ''
    }
  },
  async mounted() {
    await this.fetchRoot()
    await this.createTree()
  },
  methods: {
    async createTree() {
      d3.select('svg').remove()

      this.svg = d3
        .select('#d3container')
        .append('svg')
        .attr('width', 1200)
        .attr('height', 800)
        .append('g')
        .attr('transform', 'translate(0, 50)')

      const tree = await this.fetchedTreeData
      console.log(tree)
      console.log(this.treeData)
      const root = d3.hierarchy(tree)
      const treeStructure = d3.tree().size([1200, 550])
      this.information = treeStructure(root)

      this.connections = this.svg.append('g').selectAll('path').data(this.information.links())
      this.connections
        .enter()
        .append('path')
        .attr('d', (d) => {
          return 'M' + d.source.x + ',' + d.source.y + ' v 55 H' + d.target.x + ' V ' + d.target.y
        })

      const rectangles = this.svg.append('g').selectAll('rect').data(this.information.descendants())
      rectangles
        .enter()
        .append('rect')
        .attr('x', (d) => {
          return d.x - 40
        })
        .attr('y', (d) => {
          return d.y - 40
        })

      this.names = this.svg.append('g').selectAll('text').data(this.information.descendants())
      this.names
        .enter()
        .append('text')
        .text((d) => {
          return d.data.name + '\n' + 'id:' + d.data.id
        })
        .attr('x', (d) => {
          return d.x
        })
        .attr('y', (d) => {
          return d.y - 15
        })
    },
    async fetchRoot() {
      const response = await axios.get('api/person/root')
      console.log(response.data)
      this.fetchedTreeData = response.data
    },
    async addChild() {
      const response = await axios.post(
        'api/person/addChild',
        {},
        {
          params: {
            id: this.parentIdField,
            name: this.childNameField
          }
        }
      )

      if (response.status == 200) {
        console.log(response.status)
        await this.fetchRoot()
        await this.createTree()
      }
    },
    async deleteNode() {
      const response = await axios.delete('api/person/deleteNode', {
        params: { id: this.nodeIdField }
      })

      if (response.status == 200) {
        console.log(response.status)
        await this.fetchRoot()
        await this.createTree()
      }
    },
    async deleteEverything() {
      const response = await axios.delete('api/person/deleteAll')

      if (response.status == 200) {
        console.log(response.status)
        await this.fetchRoot()
        await this.createTree()
      }
    },
    async calculateSiblings() {
      const response = await axios.post(
        'api/person/countSiblings',
        {},
        {
          params: {
            id: this.calculateSiblingsIdField
          }
        }
      )

      if (response.status == 200) {
        this.siblingsCount = response.data
      }
    },
    async calculateCousins() {
      const response = await axios.post(
        'api/person/countCousins',
        {},
        {
          params: {
            id: this.calculateCousinsIdField
          }
        }
      )

      if (response.status == 200) {
        this.cousinsCount = response.data
      }
    },
    async addNode() {
      const response = await axios.post(
        'api/person/addNode',
        {},
        {
          params: {
            name: this.addNodeField
          }
        }
      )

      if (response.status == 200) {
        console.log(response.status)
        await this.fetchRoot()
        await this.createTree()
      }
    },
  },
  computed: {}
}
</script>

<style>
rect {
  fill: white;
  stroke: black;
  width: 100px;
  height: 40px;
}
path {
  fill: none;
  stroke: #770865;
}
text {
  color: white;
  text-anchor: middle;
}
</style>
