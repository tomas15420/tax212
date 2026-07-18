import { defineConfig } from 'orval';

export default defineConfig({
    tax212: {
        input: 'http://localhost:8080/v3/api-docs',
        output: {
            mode: 'split',
            target: 'src/api/tax212.ts',
            schemas: 'src/api/model',
            client: 'react-query',
            mock: false,
            baseUrl: 'http://localhost:8080',


        }
    }
});
